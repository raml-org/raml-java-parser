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
import org.raml.yagi.framework.nodes.ReferenceNode;

import javax.annotation.Nonnull;

public class ResourceRefRule extends StringTypeRule
{

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (node instanceof ReferenceNode)
        {
            return node;
        }
        else
        {
            return super.apply(node);
        }
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return super.matches(node) || node instanceof ReferenceNode;
    }
}
