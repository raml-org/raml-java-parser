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
package org.raml.v2.internal.utils;

import com.google.common.collect.Lists;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.StringNode;

public class JSonDumper
{

    public static String dump(Node node)
    {
        return dumpNode(node);
    }

    private static String dump(Node node, String start, String separator, String end)
    {
        StringBuilder builder = new StringBuilder();
        List<String> children = dumpChildren(node.getChildren());
        builder.append(start).append(StringUtils.join(children, separator)).append(end);
        return builder.toString();
    }

    private static List<String> dumpChildren(List<Node> children)
    {
        List<String> valueChildren = Lists.newArrayList();
        for (Node child : children)
        {
            valueChildren.add(dumpNode(child));
        }
        return valueChildren;
    }

    private static String dumpNode(Node child)
    {
        if (child instanceof KeyValueNode)
        {
            KeyValueNode keyValueNode = (KeyValueNode) child;
            return dumpNode(keyValueNode.getKey()) + " : " + dumpNode(keyValueNode.getValue());
        }
        else if (child instanceof ObjectNode)
        {
            return dump(child, "{\n", ",\n", "\n}");
        }
        else if (child instanceof StringNode)
        {
            return "\"" + ((StringNode) child).getValue() + "\"";
        }
        else if (child instanceof ArrayNode)
        {
            return dump(child, "[\n", ",\n", "\n]");
        }
        else
        {
            return child.toString();
        }
    }
}
