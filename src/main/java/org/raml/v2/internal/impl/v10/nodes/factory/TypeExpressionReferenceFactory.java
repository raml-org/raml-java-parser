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

import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.grammar.rule.NodeFactory;
import org.raml.v2.internal.framework.grammar.rule.NodeReferenceFactory;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.v10.nodes.ArrayTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.UnionTypeExpressionNode;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Stack;

public class TypeExpressionReferenceFactory implements NodeFactory
{


    @Override
    public Node create(Node currentNode, Object... args)
    {
        String expression = ((String) args[0]).trim();
        try
        {
            return parse(new StringCharacterIterator(expression), 0);
        }
        catch (TypeExpressionParsingException e)
        {
            return ErrorNodeFactory.createInvalidTypeExpressionSyntax(e.getMessage(), e.getLocation());
        }
    }

    public TypeExpressionNode parse(StringCharacterIterator iter, int depth) throws TypeExpressionParsingException
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
                expressionStack.push(parse(iter, depth + 1));
                break;
            case ')':
                return handleExpressionFinished(expressionStack, simpleExpression);
            case '|':
                handleSimpleExpression(expressionStack, simpleExpression);
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
                handleSimpleExpression(expressionStack, simpleExpression);
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
                    final ArrayTypeExpressionNode arrayTypeTypeNode = new ArrayTypeExpressionNode();
                    arrayTypeTypeNode.addChild(validateNode(expressionStack.pop()));
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
        return handleExpressionFinished(expressionStack, simpleExpression);
    }

    private TypeExpressionNode handleExpressionFinished(Stack<TypeExpressionNode> typeStack, StringBuilder simpleExpression) throws TypeExpressionParsingException
    {
        handleSimpleExpression(typeStack, simpleExpression);
        TypeExpressionNode result = null;
        if (typeStack.isEmpty())
        {
            throw new TypeExpressionParsingException("Invalid empty expression.", 0);
        }
        while (!typeStack.isEmpty())
        {
            final TypeExpressionNode node = typeStack.pop();
            if (result != null)
            {
                node.addChild(result);
            }
            result = node;
            validateNode(result);
        }
        return result;
    }

    private TypeExpressionNode validateNode(TypeExpressionNode result) throws TypeExpressionParsingException
    {
        if (result instanceof UnionTypeExpressionNode)
        {
            if (result.getChildren().size() < 2)
            {
                throw new TypeExpressionParsingException("Invalid union type expression.", 0);
            }
        }
        else if (result instanceof ArrayTypeExpressionNode)
        {
            if (result.getChildren().size() != 1)
            {
                throw new TypeExpressionParsingException("Invalid array type expression.", 0);
            }
        }
        return result;
    }

    private void handleSimpleExpression(Stack<TypeExpressionNode> expressions, StringBuilder simpleExpression)
    {

        // If it is a native type we should not create a reference node
        if (NativeTypeExpressionNode.isNativeType(simpleExpression.toString()))
        {
            expressions.push(new NativeTypeExpressionNode(simpleExpression.toString()));
        }
        else
        {
            final NodeReferenceFactory nodeReferenceFactory = new NodeReferenceFactory(NamedTypeExpressionNode.class);
            if (simpleExpression.length() > 0)
            {
                final Node parse = nodeReferenceFactory.parse(simpleExpression.toString());
                expressions.push((TypeExpressionNode) parse);
            }
        }
        clear(simpleExpression);
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
