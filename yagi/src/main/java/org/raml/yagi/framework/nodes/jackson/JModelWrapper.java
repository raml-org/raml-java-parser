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
package org.raml.yagi.framework.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;

import java.util.Iterator;
import java.util.Map;

public class JModelWrapper
{
    private ResourceLoader resourceLoader;
    private String resourcePath;

    public JModelWrapper(ResourceLoader resourceLoader, String resourcePath)
    {
        this.resourceLoader = resourceLoader;
        this.resourcePath = resourcePath;
    }

    public Node wrap(JsonNode node)
    {
        switch (node.getNodeType())
        {
        case ARRAY:
            return wrap((ArrayNode) node);
        case OBJECT:
            return wrap((ObjectNode) node);
        case BOOLEAN:
            return wrap((BooleanNode) node);
        case NULL:
            return wrap((NullNode) node);
        case NUMBER:
            if (node instanceof IntNode)
            {
                return wrap((IntNode) node);
            }
            return wrap((NumericNode) node);
        case STRING:
            return wrap((TextNode) node);
        default:
            return null;
        }
    }

    private JObjectNode wrap(ObjectNode objectNode)
    {
        JObjectNode object = new JObjectNode(objectNode, resourcePath, resourceLoader);
        Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();

        while (fields.hasNext())
        {
            Map.Entry<String, JsonNode> node = fields.next();
            Node key = wrap(new TextNode(node.getKey()));
            Node value = wrap(node.getValue());
            KeyValueNodeImpl keyValue = new KeyValueNodeImpl(key, value);
            object.addChild(keyValue);
        }
        return object;
    }

    private JArrayNode wrap(ArrayNode arrayNode)
    {
        JArrayNode array = new JArrayNode(arrayNode, resourcePath, resourceLoader);
        for (JsonNode node : arrayNode)
        {
            array.addChild(wrap(node));
        }
        return array;
    }

    private JStringNode wrap(TextNode stringNode)
    {
        return new JStringNode(stringNode, resourcePath, resourceLoader);
    }

    private JIntegerNode wrap(IntNode integerNode)
    {
        return new JIntegerNode(integerNode, resourcePath, resourceLoader);
    }

    private JFloatingNode wrap(NumericNode numericNode)
    {
        return new JFloatingNode(numericNode, resourcePath, resourceLoader);
    }

    private JNullNode wrap(NullNode nullNode)
    {
        return new JNullNode(nullNode, resourcePath, resourceLoader);
    }

    private JBooleanNode wrap(BooleanNode booleanNode)
    {
        return new JBooleanNode(booleanNode, resourcePath, resourceLoader);
    }
}
