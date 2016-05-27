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


import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.suggester.DefaultSuggestion;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;

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
    private String suggestion;
    private String label;

    public RegexValueRule(Pattern value)
    {
        this.value = value;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        if (StringUtils.isEmpty(suggestion))
        {
            return Collections.emptyList();
        }
        else
        {
            return Collections.<Suggestion>singletonList(new DefaultSuggestion(suggestion, description, label));
        }
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode && getMatcher((StringNode) node).matches();
    }

    private Matcher getMatcher(StringNode node)
    {
        return value.matcher(node.getValue());
    }

    public RegexValueRule label(String value)
    {
        this.label = value;
        return this;
    }

    public RegexValueRule suggest(String value)
    {
        this.suggestion = value;
        return this;
    }

    public RegexValueRule description(String description)
    {
        this.description = description;
        return this;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidValue(node, String.valueOf(value));
        }
        final Matcher matcher = getMatcher((StringNode) node);
        final int i = matcher.groupCount();
        final List<String> groups = new ArrayList<>();
        if (i > 0)
        {
            matcher.matches();
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
