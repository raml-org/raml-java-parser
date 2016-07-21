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

import javax.annotation.Nonnull;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.util.NodeSelector;

public class ExampleDeclarationNode extends KeyValueNodeImpl implements OverlayableNode
{
    public ExampleDeclarationNode()
    {
    }

    private ExampleDeclarationNode(KeyValueNodeImpl node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ExampleDeclarationNode(this);
    }

    public Node getExampleValue()
    {
        if (isExplicitExample())
        {
            return NodeSelector.selectFrom("value", getValue());
        }
        else
        {
            return getValue();
        }
    }

    public String getName()
    {
        final Node ancestor = org.raml.yagi.framework.util.NodeUtils.getAncestor(this, 2);
        if (ancestor instanceof KeyValueNode && ((KeyValueNode) ancestor).getKey().toString().equals("examples"))
        {
            return getKey().toString();
        }
        else
        {
            return null;
        }
    }

    public boolean isExplicitExample()
    {
        if (getValue() instanceof ObjectNode)
        {
            final Node value = NodeSelector.selectFrom("value", getValue());
            if (value != null)
            {
                return true;
            }
        }
        return false;
    }

    @Nonnull
    public Boolean isStrict()
    {
        if (isExplicitExample())
        {
            final Boolean strict = NodeSelector.selectBooleanValue("strict", getValue());
            return strict != null ? strict : true;
        }
        else
        {
            return true;
        }
    }

}
