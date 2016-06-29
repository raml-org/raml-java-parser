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
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class StringValueRule extends Rule
{

    private String value;
    private String description;

    public StringValueRule(String value)
    {
        this.value = value;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Collections.<Suggestion> singletonList(new DefaultSuggestion(value, description, StringUtils.capitalize(value)));
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
    }

    public StringValueRule description(String description)
    {
        this.description = description;
        return this;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!(node instanceof StringNode))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.String);
        }
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidValue(node, value);
        }
        return createNodeUsingFactory(node, ((StringNode) node).getValue());

    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public String getDescription()
    {
        return "\"" + value + "\"";
    }

    public String getValue()
    {
        return value;
    }
}
