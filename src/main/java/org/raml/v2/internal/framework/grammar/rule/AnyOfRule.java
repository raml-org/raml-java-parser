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

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AnyOfRule extends Rule
{

    protected List<Rule> rules;


    public AnyOfRule(@Nonnull List<Rule> rules)
    {
        if (rules.isEmpty())
        {
            throw new IllegalArgumentException("rules cannot be empty");
        }
        this.rules = rules;
    }

    public AnyOfRule(Rule... rules)
    {
        this(Arrays.asList(rules));
    }

    @Override
    @Nonnull
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        final List<Suggestion> result = new ArrayList<>();
        for (Rule rule : rules)
        {
            result.addAll(rule.getSuggestions(node, context));
        }
        return result;
    }

    @Nullable
    public Rule getMatchingRule(Node node)
    {
        for (Rule rule : rules)
        {
            if (rule.matches(node))
            {
                return rule;
            }
        }
        return null;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        for (Rule rule : rules)
        {
            if (rule.matches(node))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        for (Rule rule : rules)
        {
            if (rule.matches(node))
            {
                return createNodeUsingFactory(rule.apply(node));
            }
        }

        return ErrorNodeFactory.createInvalidNode(node);
    }

    @Override
    public String getDescription()
    {
        final StringBuilder desc = new StringBuilder();
        desc.append("Any of :");
        int i = 0;
        for (Rule rule : rules)
        {
            if (i > 0)
            {
                desc.append(",");
            }
            desc.append(rule.getDescription());
            i++;
        }
        return desc.toString();
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, RamlParsingContext context)
    {
        if (!pathToRoot.isEmpty())
        {
            final Node peek = pathToRoot.get(0);
            final Rule innerRule = getMatchingRule(peek);
            if (innerRule != null)
            {
                final List<Suggestion> suggestions = innerRule.getSuggestions(pathToRoot, context);
                if (suggestions.isEmpty())
                {
                    return getSuggestions(peek, context);
                }
                else
                {
                    return suggestions;
                }
            }
            else
            {
                return getSuggestions(peek, context);
            }

        }
        else
        {
            return Collections.emptyList();
        }
    }
}
