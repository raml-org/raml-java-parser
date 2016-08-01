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
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Accepts if any rule matches and suggests all the possibilities.
 */
public class AnyOfRule extends Rule
{

    protected List<Rule> rules;


    public AnyOfRule(@Nonnull List<Rule> rules)
    {
        this.rules = new ArrayList<>(rules);
    }

    public AnyOfRule(Rule... rules)
    {
        this(Arrays.asList(rules));
    }

    public List<Rule> getRules()
    {
        return rules;
    }

    @Override
    @Nonnull
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        final List<Suggestion> result = new ArrayList<>();
        for (Rule rule : rules)
        {
            result.addAll(rule.getSuggestions(node, context));
        }
        return result;
    }

    public AnyOfRule add(Rule rule)
    {
        this.rules.add(rule);
        return this;
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
    public List<Rule> getChildren()
    {
        return rules;
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
                final Node appliedNode = rule.apply(node);
                return createNodeUsingFactory(appliedNode);
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
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, ParsingContext context)
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

    public AnyOfRule addAll(List<Rule> rules)
    {
        this.rules.addAll(rules);
        return this;
    }
}
