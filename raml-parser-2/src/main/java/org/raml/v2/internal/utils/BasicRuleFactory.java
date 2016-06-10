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
package org.raml.v2.internal.utils;

import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.yagi.framework.grammar.rule.NullValueRule;
import org.raml.yagi.framework.grammar.rule.RegexValueRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;

import java.util.regex.Pattern;

public class BasicRuleFactory
{

    public static StringValueRule stringValue(String value)
    {
        return new StringValueRule(value);
    }

    public static RegexValueRule regexValue(String value)
    {
        return new RegexValueRule(Pattern.compile(value));
    }

    public static NullValueRule nullValue()
    {
        return new NullValueRule();
    }

    public static KeyValueRule property(String key, Rule value)
    {
        return new KeyValueRule(stringValue(key), value);
    }

    public static KeyValueRule patternProperty(String key, Rule value)
    {
        return new KeyValueRule(regexValue(key), value);
    }


}
