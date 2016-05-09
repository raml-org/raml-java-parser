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
package org.raml.v2.internal.impl.v10.nodes.types.factories;

import org.raml.v2.internal.framework.grammar.rule.NodeFactory;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.BooleanTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.DateTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.FileTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.FloatTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.IntegerTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.StringTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.UnionTypeNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.v2.internal.utils.NodeUtils;

public class TypeNodeFactory implements NodeFactory
{

    @Override
    public Node create(Object... args)
    {

        Node typeNode = NodeUtils.getType((Node) args[0]);
        if (typeNode instanceof SYArrayNode)
        {
            return new ObjectTypeNode();
        }
        StringNode type = typeNode instanceof StringNode ? (StringNode) typeNode : null;
        if (type == null)
        {
            return new StringTypeNode();
        }
        else
        {
            String value = type.getValue();
            return createNodeFromType(value);
        }
    }

    public static Node createNodeFromType(String value)
    {
        switch (value)
        {
        case "string":
            return new StringTypeNode();
        case "number":
            return new FloatTypeNode();
        case "integer":
            return new IntegerTypeNode();
        case "boolean":
            return new BooleanTypeNode();
        case "file":
            return new FileTypeNode();
        case "object":
            return new ObjectTypeNode();
        case "date-only":
        case "time-only":
        case "datetime-only":
        case "datetime":
            return new DateTypeNode();
        default:
            return new UnionTypeNode();
        }
    }
}
