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

import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.nodes.ExampleTypeNode;
import org.raml.v2.internal.impl.commons.nodes.MultipleExampleTypeNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.TypeNode;

public class ExampleSpec extends Annotable
{

    private KeyValueNode node;

    public ExampleSpec(KeyValueNode node)
    {
        this.node = node;
    }

    public String value()
    {
        return getNode() != null ? getNode().toString() : null;
    }

    public String name()
    {
        if ((node.getParent() instanceof MultipleExampleTypeNode))
        {
            return String.valueOf(node.getKey());
        }
        return null;
    }

    public TypeInstance structuredValue()
    {
        Node value = node.getValue();
        // FIXME ExampleTypeNode may wrap a SimpleTypeNode
        if ((value instanceof ExampleTypeNode) && value.getSource() != null)
        {
            value = value.getSource();
        }
        return new TypeInstance(value);
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }
}
