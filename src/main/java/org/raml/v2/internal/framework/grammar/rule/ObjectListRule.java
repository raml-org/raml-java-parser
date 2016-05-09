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
package org.raml.v2.internal.framework.grammar.rule;

import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;

public class ObjectListRule extends Rule
{

    private Rule rule;

    public ObjectListRule(Rule rule)
    {
        this.rule = rule;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof ArrayNode;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        for (Node child : node.getChildren())
        {
            rule.apply(child);
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return null;
    }
}
