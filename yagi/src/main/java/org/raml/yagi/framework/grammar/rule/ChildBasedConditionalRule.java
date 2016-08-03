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
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Delegates to a rule if the specified condition matches the first child.
 */
public class ChildBasedConditionalRule extends Rule
{

    private Rule discriminator;
    private Rule delegate;

    public ChildBasedConditionalRule(Rule condition, Rule delegate)
    {
        this.discriminator = condition;
        this.delegate = delegate;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, ParsingContext context)
    {
        if (discriminator.matches(pathToRoot.get(0)))
        {
            return delegate.getSuggestions(pathToRoot, context);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return delegate.getSuggestions(node, context);
    }

    @Override
    public List<? extends Rule> getChildren()
    {
        return Collections.singletonList(delegate);
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        List<Node> children = node.getChildren();
        boolean matches = delegate.matches(node);
        return matches && (children.isEmpty() || discriminator.matches(children.get(0)));
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        return delegate.apply(node);
    }

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }
}
