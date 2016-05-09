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

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.FloatingNode;
import org.raml.v2.internal.framework.nodes.IntegerNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;

public class RangeValueRule extends Rule
{

    private Number maximumValue;
    private Number minimumValue;

    public RangeValueRule(Number minimumValue, Number maximumValue)
    {
        this.minimumValue = minimumValue;
        this.maximumValue = maximumValue;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        if (node instanceof IntegerNode)
        {
            return ((IntegerNode) node).getValue().compareTo(minimumValue.intValue()) >= 0 &&
                   ((IntegerNode) node).getValue().compareTo(maximumValue.intValue()) <= 0;
        }
        return node instanceof FloatingNode && ((FloatingNode) node).getValue().compareTo(new BigDecimal(minimumValue.intValue())) >= 0 &&
               ((FloatingNode) node).getValue().compareTo(new BigDecimal(maximumValue.intValue())) <= 0;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            return createNodeUsingFactory(node, ((SimpleTypeNode) node).getValue());
        }
        else
        {
            return ErrorNodeFactory.createInvalidRangeValue(minimumValue, maximumValue);
        }
    }

    @Override
    public String getDescription()
    {
        return "Maximum value";
    }
}
