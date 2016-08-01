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
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConditionalRules
{

    private List<String> selectorExpression;
    private List<ConditionalRule> options;
    private DefaultValue defaultValue;

    /**
     * Creates a rule that depends on another rule on your grammar
     * @param selectorExpression expression to obtain the node that will be used for the pattern matching
     * @param cases Conditional cases
     */
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

    public List<? extends Rule> getChildren()
    {
        final ArrayList<Rule> result = new ArrayList<>();
        for (ConditionalRule option : options)
        {
            result.addAll(new ArrayList<>(option.getRules()));
        }
        return result;
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

    /**
     * This method lets you use a value node to match the when clause when there is no other rule that matches.
     * @param defaultValue value node.
     */
    public ConditionalRules defaultValue(Node defaultValue)
    {
        this.defaultValue = new LiteralDefaultValue(defaultValue);
        return this;
    }

}
