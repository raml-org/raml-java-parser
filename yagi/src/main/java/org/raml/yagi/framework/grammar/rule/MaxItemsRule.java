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
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.RamlParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class MaxItemsRule extends Rule
{
    private int maxLength;

    public MaxItemsRule(int maxLength)
    {
        this.maxLength = maxLength;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node.getChildren().size() < maxLength;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            return createNodeUsingFactory(node, ((StringNode) node).getValue());
        }
        else
        {
            return ErrorNodeFactory.createInvalidMaxItems(maxLength);
        }
    }

    @Override
    public String getDescription()
    {
        return "Max amount of items " + maxLength;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Collections.emptyList();
    }
}
