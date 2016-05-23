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
import org.raml.v2.internal.utils.NodeSelector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConditionalRules
{

    private List<String> selectorExpression;
    private List<ConditionalRule> options;
    private DefaultValue defaultValue;


    public ConditionalRules(List<String> selectorExpression, ConditionalRule... cases)
    {
        this.selectorExpression = selectorExpression;
        this.options = Arrays.asList(cases);
    }

    @Nonnull
    public List<KeyValueRule> getRulesNode(Node node)
    {
        Node from = selectValue(node);
        if (from == null && defaultValue != null)
        {
            from = defaultValue.getDefaultValue(node);
        }

        if (from != null)
        {
            for (ConditionalRule option : options)
            {
                if (option.matches(from))
                {
                    return option.getRules();
                }
            }
        }

        return Collections.emptyList();
    }

    @Nullable
    private Node selectValue(Node node)
    {

        for (String expr : selectorExpression)
        {
            Node from = NodeSelector.selectFrom(expr, node);
            if (from != null)
            {
                return from;
            }
        }
        return null;
    }

    public ConditionalRules defaultValue(DefaultValue defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    public ConditionalRules defaultValue(Node defaultValue)
    {
        this.defaultValue = new LiteralDefaultValue(defaultValue);
        return this;
    }

}
