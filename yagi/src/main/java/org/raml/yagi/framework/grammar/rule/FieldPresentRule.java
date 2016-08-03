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
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * Delegates to a rule if a selector expression returns a value
 */
public class FieldPresentRule extends Rule
{

    private String selector;

    private Rule delegate;

    public FieldPresentRule(String selector, Rule then)
    {
        this.selector = selector;
        this.delegate = then;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, ParsingContext context)
    {
        return delegate.getSuggestions(pathToRoot, context);
    }


    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return delegate.getSuggestions(node, context);
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return isPresent(node) && delegate.matches(node);
    }

    private boolean isPresent(@Nonnull Node node)
    {
        return NodeSelector.selectFrom(selector, node) != null;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (isPresent(node))
        {
            return delegate.apply(node);
        }
        else
        {
            return ErrorNodeFactory.createMissingField(selector);
        }
    }

    @Override
    public String getDescription()
    {
        return delegate.getDescription();
    }
}
