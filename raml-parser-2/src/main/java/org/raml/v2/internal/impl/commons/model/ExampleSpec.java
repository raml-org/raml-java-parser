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

import org.raml.v2.internal.utils.JSonDumper;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.util.NodeSelector;


public class ExampleSpec extends Annotable<KeyValueNode>
{
    public ExampleSpec(KeyValueNode node)
    {
        super(node);
    }

    public String value()
    {
        final Node exampleValue = getExampleValue();

        if (structuredValue().isScalar())
        {
            return exampleValue.toString();
        }
        else
        {
            return JSonDumper.dump(exampleValue);
        }
    }

    public String name()
    {
        return getName();
    }

    public TypeInstance structuredValue()
    {
        final Node value = getExampleValue();
        // FIXME ExampleTypeNode may wrap a SimpleTypeNode
        // if ((value instanceof ExampleTypeNode) && value.getSource() != null)
        // {
        // value = value.getSource();
        // }
        return new TypeInstance(value);
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }


    private Node getExampleValue()
    {
        if (isExplicitExample())
        {
            return NodeSelector.selectFrom("value", node.getValue());
        }
        else
        {
            return node.getValue();
        }
    }

    private String getName()
    {
        final Node ancestor = org.raml.yagi.framework.util.NodeUtils.getAncestor(node, 2);
        if (ancestor instanceof KeyValueNode && ((KeyValueNode) ancestor).getKey().toString().equals("examples"))
        {
            return node.getKey().toString();
        }
        else
        {
            return null;
        }
    }

    private boolean isExplicitExample()
    {
        if (node.getValue() instanceof ObjectNode)
        {
            final Node value = NodeSelector.selectFrom("value", node.getValue());
            if (value != null)
            {
                return true;
            }
        }
        return false;
    }

}
