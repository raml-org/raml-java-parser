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
package org.raml.v2.internal.impl.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.raml.yagi.framework.model.AbstractNodeModel;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

public class TypeInstance extends AbstractNodeModel<Node>
{
    public TypeInstance(Node node)
    {
        super(node);
    }

    public List<TypeInstanceProperty> properties()
    {
        List<TypeInstanceProperty> result = new ArrayList<>();
        if (node instanceof ArrayNode)
        {
            result.add(new TypeInstanceProperty((KeyValueNode) node.getParent()));
        }
        else if (node instanceof ObjectNode)
        {
            for (Node child : node.getChildren())
            {
                result.add(new TypeInstanceProperty((KeyValueNode) child));
            }
        }
        return result;
    }

    public Boolean isScalar()
    {
        return node instanceof SimpleTypeNode;
    }

    public Object value()
    {
        if (node instanceof SimpleTypeNode)
        {
            return ((SimpleTypeNode) node).getValue();
        }
        return null;
    }

    @Override
    public Node getNode()
    {
        return node;
    }
}
