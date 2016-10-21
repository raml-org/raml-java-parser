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
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;

public class PropertyUtils
{
    public static String getName(KeyValueNodeImpl node)
    {
        final StringNode key = (StringNode) node.getKey();
        final String keyValue = key.getValue();
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

    private static Node getRequiredNode(KeyValueNode node)
    {
        return node.getValue().get("required");
    }

    public static boolean isRequired(KeyValueNode node)
    {
        final StringNode key = (StringNode) node.getKey();
        return getRequiredNode(node) instanceof BooleanNode ? ((BooleanNode) getRequiredNode(node)).getValue() : !key.getValue().endsWith("?");
    }
}
