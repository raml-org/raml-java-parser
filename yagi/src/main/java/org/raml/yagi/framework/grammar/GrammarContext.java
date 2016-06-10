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
package org.raml.yagi.framework.grammar;

import org.raml.yagi.framework.grammar.rule.Rule;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class GrammarContext
{

    private Map<String, Rule> ruleMap;

    public GrammarContext()
    {
        this.ruleMap = new HashMap<>();
    }

    public <T extends Rule> T registerRule(String name, T rule)
    {
        this.ruleMap.put(name, rule);
        return rule;
    }

    @Nullable
    public Rule getRuleByName(String name)
    {
        return ruleMap.get(name);
    }

    public boolean hasRule(String name)
    {
        return ruleMap.containsKey(name);
    }
}
