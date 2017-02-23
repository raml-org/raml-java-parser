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

import com.google.common.collect.Range;
import org.raml.yagi.framework.nodes.FloatingNode;
import org.raml.yagi.framework.nodes.IntegerNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class NumberTypeRule extends AbstractTypeRule
{
    @Nullable
    private Range<Double> range;

    public NumberTypeRule(@Nullable Range<Double> range)
    {
        this.range = range;
    }

    public NumberTypeRule()
    {
        this(null);
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Collections.emptyList();
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof FloatingNode)
        {
            return range == null || range.contains(((FloatingNode) node).getValue().doubleValue());
        }
        if (node instanceof IntegerNode)
        {
            return range == null || range.contains(((IntegerNode) node).getValue().doubleValue());
        }
        if (node instanceof StringNode)
        {
            try
            {
                Double value = Double.parseDouble(((StringNode) node).getValue());
                return range == null || range.contains(value);
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }
        return false;
    }

    @Override
    public String getDescription()
    {
        return "Number";
    }

    @Nonnull
    @Override
    NodeType getType()
    {
        return NodeType.Float;
    }
}
