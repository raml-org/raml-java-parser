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

import org.junit.Test;
import org.mockito.Mockito;
import org.raml.yagi.framework.nodes.BooleanNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.yagi.framework.nodes.jackson.JBooleanNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYBooleanNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYStringNode;

import static org.junit.Assert.*;

/**
 * Created. There, you have it.
 */
public class BooleanTypeRuleTest
{

    @Test
    public void booleanWillMatchString()
    {

        BooleanTypeRule rule = new BooleanTypeRule(true);
        assertFalse(rule.matches(new StringNodeImpl("false")));
        assertFalse(rule.matches(new StringNodeImpl("true")));
        assertFalse(rule.matches(new StringNodeImpl("anythingElse")));
    }

    @Test
    public void booleanWontMatchString()
    {

        BooleanTypeRule rule = new BooleanTypeRule(false);
        assertTrue(rule.matches(new StringNodeImpl("false")));
        assertTrue(rule.matches(new StringNodeImpl("true")));
        assertFalse(rule.matches(new StringNodeImpl("anythingElse")));
    }

    @Test
    public void booleanMatchesBoolean()
    {

        BooleanNode booleanNode = Mockito.mock(BooleanNode.class);
        BooleanTypeRule rule = new BooleanTypeRule(true);
        assertTrue(rule.matches(booleanNode));
    }

}