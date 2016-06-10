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
package org.raml.yagi.framework.grammar.rule;

import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import javax.annotation.Nonnull;

public abstract class AbstractTypeRule extends Rule
{

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            if (node instanceof SimpleTypeNode)
            {
                return createNodeUsingFactory(node, ((SimpleTypeNode) node).getValue());
            }
            else
            {
                return createNodeUsingFactory(node);
            }
        }
        else
        {
            return ErrorNodeFactory.createInvalidType(node, getType());
        }
    }

    @Nonnull
    abstract NodeType getType();
}
