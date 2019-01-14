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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.IntegerNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;

public class PropertyUtils
{
    public static String getName(KeyValueNode node)
    {
        final String keyValue = getValueAsString(node);
        if (getRequiredNode(node) == null)
        {
            // If required field is set then the ? should be ignored
            return keyValue.endsWith("?") ? keyValue.substring(0, keyValue.length() - 1) : keyValue;
        }
        else
        {
            return keyValue;
        }
    }

    private static String getValueAsString(KeyValueNode node)
    {
        final String keyValue;
        if (node.getKey() instanceof IntegerNode)
        {
            keyValue = String.valueOf(((IntegerNode) node.getKey()).getValue());
        }
        else
        {
            StringNode key = (StringNode) node.getKey();
            keyValue = key.getValue();
        }
        return keyValue;
    }

    private static Node getRequiredNode(KeyValueNode node)
    {
        return node.getValue().get("required");
    }

    public static boolean isRequired(KeyValueNode node)
    {
        return getRequiredNode(node) instanceof BooleanNode ? ((BooleanNode) getRequiredNode(node)).getValue() : !getValueAsString(node).endsWith("?");
    }
}
