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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ConditionalRule
{
    private Rule condition;
    private List<KeyValueRule> rules;

    public ConditionalRule(Rule condition)
    {
        this.condition = condition;
        this.rules = new ArrayList<>();
    }

    public boolean matches(@Nonnull Node node)
    {
        return condition.matches(node);
    }

    public ConditionalRule add(@Nonnull KeyValueRule rule)
    {
        rules.add(rule);
        return this;
    }

    @Nonnull
    public List<KeyValueRule> getRules()
    {
        return rules;
    }
}
