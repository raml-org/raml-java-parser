/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.v2.internal.impl.v10.nodes.factory;

import org.raml.v2.internal.impl.v10.type.TypeId;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.NodeFactory;
import org.raml.v2.internal.impl.commons.rule.NodeReferenceFactory;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.ArrayTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.UnionTypeExpressionNode;

import javax.annotation.Nonnull;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Stack;

public class TypeExpressionReferenceFactory implements NodeFactory
{


    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        String expression = ((String) args[0]).trim();
        try
        {
            return parse(currentNode, new StringCharacterIterator(expression), 0);
        }
        catch (TypeExpressionParsingException e)
        {
            return ErrorNodeFactory.createInvalidTypeExpressionSyntax(e.getMessage(), expression, e.getLocation());
        }
    }

    public TypeExpressionNode parse(Node currentNode, StringCharacterIterator iter, int depth) throws TypeExpressionParsingException
    {
        // Precedence
        final Stack<TypeExpressionNode> expressionStack = new Stack<>();
        final StringBuilder simpleExpression = new StringBuilder();
        for (char c = iter.current(); c != CharacterIterator.DONE; c = iter.next())
        {
            switch (c)
            {
            case '(':
                iter.next();
                expressionStack.push(parse(currentNode, iter, depth + 1));
                break;
            case ')':
                return handleExpressionFinished(currentNode, expressionStack, iter, simpleExpression);
            case '|':
                handleSimpleExpression(currentNode, iter, expressionStack, simpleExpression);
                if (expressionStack.isEmpty())
                {
                    throw new TypeExpressionParsingException("Expecting a type expression before |.", iter.getIndex());
                }
                else
                {
                    final TypeExpressionNode pop = expressionStack.pop();
                    if (expressionStack.isEmpty() || !(expressionStack.peek() instanceof UnionTypeExpressionNode))
                    {
                        expressionStack.push(new UnionTypeExpressionNode());
                    }
                    expressionStack.peek().addChild(pop);
                }
                break;
            case '[':
                handleSimpleExpression(currentNode, iter, expressionStack, simpleExpression);
                if (expressionStack.isEmpty())
                {
                    throw new TypeExpressionParsingException("Expecting a type expression before [.", iter.getIndex());
                }
                else if (iter.next() != ']')
                {
                    throw new TypeExpressionParsingException("Invalid character '" + iter.current() + "' expecting ']'.", iter.getIndex());
                }
                else
                {
                    final TypeExpressionNode arrayType = validateNode(expressionStack.pop(), iter);
                    final ArrayTypeExpressionNode arrayTypeTypeNode = new ArrayTypeExpressionNode(arrayType);
                    arrayTypeTypeNode.setStartPosition(arrayType.getStartPosition());
                    arrayTypeTypeNode.setEndPosition(arrayType.getEndPosition().rightShift(2)); // We shift 2 because is the [] of the array
                    expressionStack.push(arrayTypeTypeNode);
                }
                break;
            case ' ':
            case '\t':
            case '\n':
                // Ignore white spaces
                break;
            default:
                simpleExpression.append(c);
            }
        }
        if (depth > 0)
        {
            throw new TypeExpressionParsingException("Parenthesis are not correctly balanced.", iter.getIndex());
        }
        return handleExpressionFinished(currentNode, expressionStack, iter, simpleExpression);
    }

    private TypeExpressionNode handleExpressionFinished(Node currentNode, Stack<TypeExpressionNode> typeStack, StringCharacterIterator iter, StringBuilder simpleExpression)
            throws TypeExpressionParsingException
    {
        handleSimpleExpression(currentNode, iter, typeStack, simpleExpression);
        TypeExpressionNode result = null;
        if (typeStack.isEmpty())
        {
            throw new TypeExpressionParsingException("Invalid empty expression.", iter.getIndex());
        }
        while (!typeStack.isEmpty())
        {
            final TypeExpressionNode node = typeStack.pop();
            if (result != null)
            {
                node.addChild(result);
            }
            result = node;
            validateNode(result, iter);
        }
        return result;
    }

    private TypeExpressionNode validateNode(TypeExpressionNode result, StringCharacterIterator iter) throws TypeExpressionParsingException
    {
        if (result instanceof UnionTypeExpressionNode)
        {
            if (result.getChildren().size() < 2)
            {
                throw new TypeExpressionParsingException("Invalid union type expression.", iter.getIndex());
            }
        }
        else if (result instanceof ArrayTypeExpressionNode)
        {
            if (result.getChildren().size() != 1)
            {
                throw new TypeExpressionParsingException("Invalid array type expression.", iter.getIndex());
            }
        }
        return result;
    }

    private void handleSimpleExpression(Node currentNode, StringCharacterIterator iter, Stack<TypeExpressionNode> expressions, StringBuilder simpleExpression) throws TypeExpressionParsingException
    {
        String expressionString = simpleExpression.toString();
        boolean optionalType = false;
        if (expressionString.endsWith("?"))
        {
            expressionString = expressionString.substring(0, expressionString.length() - 1);
            optionalType = true;
        }

        final Position startPosition = currentNode.getStartPosition().rightShift(iter.getIndex() - expressionString.length());
        final Position endPosition = currentNode.getStartPosition().rightShift(iter.getIndex());

        // If it is a native type we should not create a reference node
        if (NativeTypeExpressionNode.isNativeType(expressionString))
        {
            final NativeTypeExpressionNode item = new NativeTypeExpressionNode(expressionString);
            item.setStartPosition(startPosition);
            item.setEndPosition(endPosition);
            expressions.push(item);
            if (optionalType)
            {
                handleOptionalType(expressions);
            }
        }
        else if (expressionString.length() > 0)
        {
            final NodeReferenceFactory nodeReferenceFactory = new NodeReferenceFactory(NamedTypeExpressionNode.class);
            final Node parse = nodeReferenceFactory.parse(currentNode, expressionString, iter.getIndex());
            if (parse instanceof ErrorNode)
            {
                throw new TypeExpressionParsingException(((ErrorNode) parse).getErrorMessage(), currentNode.getStartPosition().getIndex());
            }
            expressions.push((TypeExpressionNode) parse);
            if (optionalType)
            {
                handleOptionalType(expressions);
            }
        }
        clear(simpleExpression);


    }

    private void handleOptionalType(Stack<TypeExpressionNode> expressions)
    {
        final TypeExpressionNode optionalOf = expressions.pop();
        final UnionTypeExpressionNode optionalTypeExpression = new UnionTypeExpressionNode();
        optionalTypeExpression.addChild(optionalOf);
        optionalTypeExpression.addChild(new NativeTypeExpressionNode(TypeId.NULL.getType()));
        expressions.push(optionalTypeExpression);
    }

    /**
     * Clear the string builder
     * @param simpleExpression the string builder to be cleared
     */
    private void clear(StringBuilder simpleExpression)
    {
        simpleExpression.setLength(0);
    }

    private static class TypeExpressionParsingException extends Exception
    {
        private int location;

        public TypeExpressionParsingException(String message, int location)
        {
            super(message);
            this.location = location;
        }

        public int getLocation()
        {
            return location;
        }
    }
}
