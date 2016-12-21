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
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

public class TypeInstanceProperty extends AbstractNodeModel<KeyValueNode>
{
    public TypeInstanceProperty(KeyValueNode node)
    {
        super(node);
    }

    public String name()
    {
        return ((SimpleTypeNode) node.getKey()).getLiteralValue();
    }

    public TypeInstance value()
    {
        if (!isArray())
        {
            return new TypeInstance(node.getValue());
        }
        return null;
    }

    public List<TypeInstance> values()
    {
        List<TypeInstance> result = new ArrayList<>();
        if (isArray())
        {
            for (Node child : node.getValue().getChildren())
            {
                result.add(new TypeInstance(child));
            }
        }
        return result;
    }

    public Boolean isArray()
    {
        return node.getValue() instanceof ArrayNode;
    }

    @Override
    public Node getNode()
    {
        return node;
    }
}
