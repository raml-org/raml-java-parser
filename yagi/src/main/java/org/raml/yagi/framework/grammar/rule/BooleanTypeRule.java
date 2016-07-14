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

import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class BooleanTypeRule extends AbstractTypeRule
{
    private final static String TRUE = "true";
    private final static String FALSE = "false";

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Arrays.<Suggestion> asList(new DefaultSuggestion("true", "Boolean true", "true"), new DefaultSuggestion("false", "Boolean false", "false"));
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof StringNode)
        {
            String value = ((StringNode) node).getValue();
            return TRUE.equals(value) || FALSE.equals(value);
        }
        return node instanceof BooleanNode;
    }


    @Override
    public String getDescription()
    {
        return "Boolean";
    }

    @Nonnull
    @Override
    NodeType getType()
    {
        return NodeType.Boolean;
    }
}
