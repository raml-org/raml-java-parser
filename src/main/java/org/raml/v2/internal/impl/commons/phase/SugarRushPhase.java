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
package org.raml.v2.internal.impl.commons.phase;

import static org.raml.v2.internal.utils.NodeUtils.getType;

import java.util.List;

import org.raml.v2.internal.impl.v10.grammar.BuiltInScalarType;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.FloatTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.IntegerTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NullNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYNullNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYStringNode;
import org.raml.v2.internal.framework.phase.Phase;
import org.raml.v2.internal.utils.NodeUtils;

public class SugarRushPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        sweetenBuiltInTypes(tree);
        sweetenObjects(tree);
        sweetenAnnotations(tree);
        return tree;
    }

    private void sweetenBuiltInTypes(Node tree)
    {
        final List<StringNode> basicSugar = tree.findDescendantsWith(StringNode.class);

        for (StringNode sugarNode : basicSugar)
        {
            if (BuiltInScalarType.isBuiltInScalarType(sugarNode.getValue()) && !isTypePresentBasic(sugarNode))
            {
                handleBuiltInType(sugarNode);
            }
            else if ("array".equals(sugarNode.getValue()))
            {
                handleArray(sugarNode);
            }
            else if (isArraySugar(sugarNode))
            {
                handleObjectArray(sugarNode);
            }
            else if (isStringSugar(sugarNode))
            {
                setTypeString(sugarNode.getParent());
            }
        }
    }

    private boolean isStringSugar(StringNode sugarNode)
    {
        Node ancestor = NodeUtils.getAncestor(sugarNode, 3);
        Node parent = sugarNode.getParent();
        return parent instanceof KeyValueNode && ((KeyValueNode) parent).getValue() instanceof NullNode &&
               ancestor instanceof KeyValueNode && ((KeyValueNode) ancestor).getKey() instanceof StringNode &&
               ("types".equals(((StringNode) (((KeyValueNode) ancestor).getKey())).getValue()) ||
                "schemas".equals(((StringNode) (((KeyValueNode) ancestor).getKey())).getValue()) ||
               "properties".equals(((StringNode) (((KeyValueNode) ancestor).getKey())).getValue()));
    }

    private void sweetenObjects(Node tree)
    {
        final List<StringNode> basicSugar = tree.findDescendantsWith(StringNode.class);
        for (StringNode sugarNode : basicSugar)
        {
            if ("properties".equals(sugarNode.getValue()))
            {
                if (!isTypePresentObject(sugarNode))
                {
                    Node grandParent = sugarNode.getParent().getParent();
                    grandParent.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl("object")));
                }
            }
        }
    }

    private void sweetenAnnotations(Node tree)
    {
        Node annotationsNode = tree.get("annotationTypes");
        if (annotationsNode != null)
        {
            for (Node annotation : annotationsNode.getChildren())
            {
                if (isTypeMissingInAnnotation(annotation))
                {
                    if (isStringAnnotation(annotation))
                    {
                        setTypeString(annotation);
                    }
                }
            }
        }
    }

    private void handleObjectArray(StringNode sugarNode)
    {
        Node parent = sugarNode.getParent();
        Node key = isKeyValueNode(parent) ? ((KeyValueNode) parent).getKey() : null;
        String keyString = key instanceof StringNode ? ((StringNode) key).getValue() : null;
        if (parent instanceof KeyValueNode && "type".equals(keyString))
        {
            Node grandParent = parent.getParent();
            grandParent.removeChild(parent);
            KeyValueNodeImpl items = handleArraySugar(sugarNode, grandParent);
            items.setSource(parent);
            grandParent.addChild(items);
        }
        else
        {
            Node newNode = new ObjectTypeNode();
            KeyValueNodeImpl items = handleArraySugar(sugarNode, newNode);
            items.setSource(parent);
            newNode.addChild(items);
            sugarNode.replaceWith(newNode);
        }

    }

    private boolean isArraySugar(StringNode sugarNode)
    {
        return sugarNode.getValue() != null && sugarNode.getValue().endsWith("[]");
    }

    private void handleArray(StringNode sugarNode)
    {
        if (sugarNode.getParent() != null && sugarNode.getParent().getParent() != null)
        {
            Node itemsNode = sugarNode.getParent().getParent().get("items");
            if (itemsNode instanceof SYNullNode)
            {
                itemsNode.replaceWith(new StringNodeImpl(new StringNodeImpl("string")));
            }
        }
    }

    private KeyValueNodeImpl handleArraySugar(StringNode sugarNode, Node grandParent)
    {
        String value = sugarNode.getValue().split("\\[")[0];
        grandParent.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl("array")));
        return new KeyValueNodeImpl(new StringNodeImpl("items"), new StringNodeImpl(value));
    }

    private void handleBuiltInType(StringNode sugarNode)
    {
        if (sugarNode.getChildren().isEmpty())
        {
            Node newNode = getSugarNode(sugarNode.getValue());
            if (newNode != null)
            {
                newNode.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl(sugarNode.getValue())));
                sugarNode.replaceWith(newNode);
            }
        }
    }

    private void setTypeString(Node annotation)
    {
        if (isKeyValueNode(annotation))
        {
            Node stringTypeNode = new StringTypeNode();
            stringTypeNode.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), new StringNodeImpl("string")));
            ((KeyValueNode) annotation).getValue().replaceWith(stringTypeNode);
        }
    }

    private boolean isStringAnnotation(Node annotation)
    {
        return isKeyValueNode(annotation) && ((KeyValueNode) annotation).getValue().get("properties") == null;
    }

    private boolean isKeyValueNode(Node annotation)
    {
        return annotation instanceof KeyValueNode;
    }

    private boolean isTypeMissingInAnnotation(Node annotation)
    {
        return (isKeyValueNode(annotation) && getType(((KeyValueNode) annotation).getValue()) == null);
    }

    private boolean isTypePresentBasic(Node sugarNode)
    {
        Node parent = sugarNode.getParent();
        if (isKeyValueNode(parent) && ((KeyValueNode) parent).getKey() instanceof SYStringNode)
        {
            SYStringNode key = (SYStringNode) ((KeyValueNode) parent).getKey();
            return "type".equals(key.getValue());
        }
        return false;
    }

    private boolean isTypePresentObject(Node sugarNode)
    {
        return getType(sugarNode.getParent().getParent()) != null;
    }

    private Node getSugarNode(String typeNode)
    {
        if (BuiltInScalarType.STRING.getType().equals(typeNode))
        {
            return new StringTypeNode();
        }
        else if (BuiltInScalarType.NUMBER.getType().equals(typeNode))
        {
            return new FloatTypeNode();
        }
        else if (BuiltInScalarType.INTEGER.getType().equals(typeNode))
        {
            return new IntegerTypeNode();
        }
        else if (BuiltInScalarType.BOOLEAN.getType().equals(typeNode))
        {
            return new BooleanTypeNode();
        }
        else if ("object".equals(typeNode))
        {
            return new ObjectTypeNode();
        }
        else
        {
            return null;
        }
    }
}
