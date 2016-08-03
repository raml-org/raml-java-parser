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


import org.apache.commons.lang.StringUtils;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValueRule extends Rule
{

    private Pattern value;
    private String description;
    private List<String> suggestions = new ArrayList<>();
    private String label;
    private boolean fullMatch = true;

    public RegexValueRule(Pattern value)
    {
        this.value = value;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        if (suggestions.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            List<Suggestion> result = new ArrayList<>();
            for (String suggestion : suggestions)
            {
                String label = this.label;
                if (StringUtils.isBlank(label))
                {
                    label = suggestion;
                }
                result.add(new DefaultSuggestion(suggestion, description, label));
            }
            return result;
        }
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof SimpleTypeNode && (fullMatch ? getMatcher((SimpleTypeNode) node).matches() : getMatcher((SimpleTypeNode) node).find());
    }

    private Matcher getMatcher(SimpleTypeNode node)
    {
        return value.matcher(node.getLiteralValue());
    }

    public RegexValueRule label(String value)
    {
        this.label = value;
        return this;
    }

    public RegexValueRule suggest(String value)
    {
        this.suggestions.add(value);
        return this;
    }

    public RegexValueRule description(String description)
    {
        this.description = description;
        return this;
    }

    public RegexValueRule fullMatch(boolean fullMatch)
    {
        this.fullMatch = fullMatch;
        return this;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidValue(node, String.valueOf(value));
        }
        final Matcher matcher = getMatcher((SimpleTypeNode) node);
        final int i = matcher.groupCount();
        final List<String> groups = new ArrayList<>();
        if (i > 0)
        {
            if (fullMatch)
            {
                matcher.matches();
            }
            else
            {
                matcher.find();
            }
        }
        for (int j = 1; j <= i; j++)
        {
            final String group = matcher.group(j);
            groups.add(group);
        }
        return createNodeUsingFactory(node, (String[]) groups.toArray(new String[groups.size()]));
    }

    @Override
    public String getDescription()
    {
        return "/" + value.pattern() + "/";
    }
}
