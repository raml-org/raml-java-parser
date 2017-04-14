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

import org.apache.commons.lang.math.Range;
import org.raml.yagi.framework.nodes.FloatingNode;
import org.raml.yagi.framework.nodes.IntegerNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class RangeValueRule extends Rule
{

    private Range range;

    public RangeValueRule(Range range)
    {
        this.range = range;
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
        if (node instanceof IntegerNode || node instanceof FloatingNode)
        {
            return true;
        }
        else if (node instanceof SimpleTypeNode)
        {
            try
            {
                Long.parseLong(((SimpleTypeNode) node).getLiteralValue());
                return true;
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (validate(node))
        {
            return createNodeUsingFactory(node, ((SimpleTypeNode) node).getValue());
        }
        else
        {
            return ErrorNodeFactory.createInvalidRangeValue(node.toString(), range.getMinimumNumber(), range.getMaximumNumber());
        }
    }

    private boolean validate(Node node)
    {
        if (node instanceof IntegerNode)
        {
            Long value = ((IntegerNode) node).getValue();
            return range.containsLong(value);
        }
        else if (node instanceof FloatingNode)
        {
            BigDecimal value = ((FloatingNode) node).getValue();
            return range.containsDouble(value.doubleValue());
        }
        else if (node instanceof SimpleTypeNode)
        {
            try
            {
                long parseLong = Long.parseLong(((StringNode) node).getValue());
                return range.containsLong(parseLong);
            }
            catch (NumberFormatException ex)
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }

    @Override
    public String getDescription()
    {
        return "Maximum value";
    }
}
