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

import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.utils.NodeSelector;

import javax.annotation.Nonnull;

public class ExampleDeclarationNode extends KeyValueNodeImpl
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
            return strict != null ? strict : false;
        }
        else
        {
            return false;
        }
    }

}
