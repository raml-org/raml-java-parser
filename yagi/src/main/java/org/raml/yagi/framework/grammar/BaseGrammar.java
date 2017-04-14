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

import com.google.common.collect.Range;
import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.LongRange;
import org.raml.yagi.framework.grammar.rule.AllOfRule;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.grammar.rule.AnyValueRule;
import org.raml.yagi.framework.grammar.rule.ArrayRule;
import org.raml.yagi.framework.grammar.rule.BooleanTypeRule;
import org.raml.yagi.framework.grammar.rule.ChildBasedConditionalRule;
import org.raml.yagi.framework.grammar.rule.ConditionalRule;
import org.raml.yagi.framework.grammar.rule.ConditionalRules;
import org.raml.yagi.framework.grammar.rule.DefaultValue;
import org.raml.yagi.framework.grammar.rule.ExclusiveKeys;
import org.raml.yagi.framework.grammar.rule.FieldPresentRule;
import org.raml.yagi.framework.grammar.rule.FirstOfRule;
import org.raml.yagi.framework.grammar.rule.IntegerTypeRule;
import org.raml.yagi.framework.grammar.rule.IntegerValueRule;
import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.yagi.framework.grammar.rule.MinLengthRule;
import org.raml.yagi.framework.grammar.rule.NegativeRule;
import org.raml.yagi.framework.grammar.rule.NullValueRule;
import org.raml.yagi.framework.grammar.rule.NumberTypeRule;
import org.raml.yagi.framework.grammar.rule.ObjectRule;
import org.raml.yagi.framework.grammar.rule.ParentKeyDefaultValue;
import org.raml.yagi.framework.grammar.rule.RangeValueRule;
import org.raml.yagi.framework.grammar.rule.RegexValueRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.ScalarTypeRule;
import org.raml.yagi.framework.grammar.rule.StringTypeRule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;

import javax.annotation.Nullable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import static java.util.Collections.singletonList;

/**
 * Base class for rule based grammars.
 */
public class BaseGrammar
{

    private GrammarContext context;

    @Nullable
    private String nextRuleName;

    public BaseGrammar()
    {
        this.context = new GrammarContext();
    }

    /**
     * Matches an object type
     * @return The object rule
     */
    public ObjectRule objectType()
    {
        return register(new ObjectRule());
    }

    /**
     * Register the rule in the context if a named for this rule was set.
     * @param rule The rule to register
     * @param <T> The rule type
     * @return The specified rule
     */
    private <T extends Rule> T register(T rule)
    {
        if (nextRuleName != null)
        {
            context.registerRule(nextRuleName, rule);
            nextRuleName = null;
        }
        return rule;
    }

    /**
     * Registers a rule with the specified name. If the rule is already defined then it returns it otherwise it will invoke the rule factory to create.
     * @param name The name of the rule
     * @param ruleFactory The factory of the rule
     * @param <T> The node type
     * @return The rule
     */
    public <T extends Rule> T named(String name, RuleFactory<T> ruleFactory)
    {
        if (context.hasRule(name))
        {
            return (T) context.getRuleByName(name);
        }
        else
        {
            this.nextRuleName = name;
            return ruleFactory.create();
        }
    }


    /**
     * Returns rule created by the callable that runs a new separate context.
     * @param callable The callable to execute in a new context
     * @param <T> The type of rule it returns
     * @return The rule
     */
    public <T extends Rule> T inNewContext(Callable<T> callable)
    {
        final GrammarContext oldContext = this.context;
        this.context = new GrammarContext();
        final T result;
        try
        {
            result = callable.call();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        this.context = oldContext;
        return result;
    }


    /**
     * Delegates to a rule if the specified condition matches the first child.
     * @param condition The condition
     * @param then the rule to be delegated
     */
    public ChildBasedConditionalRule whenChildIs(Rule condition, Rule then)
    {
        return new ChildBasedConditionalRule(condition, then);
    }

    /**
     * Delegates to a rule if a selector expression returns a value
     * @param selector The selector expression
     * @param then The rule to be delegated if the selector returns a value
     */
    public FieldPresentRule whenPresent(String selector, Rule then)
    {
        return new FieldPresentRule(selector, then);
    }

    /**
     * Matches any value
     * @return Any value rule
     */
    public AnyValueRule any()
    {
        return new AnyValueRule();
    }

    /**
     * Matches an array value
     * @param of The type of the array
     * @return The array rule
     */
    public ArrayRule array(Rule of)
    {
        return new ArrayRule(of);
    }

    /**
     * Matches a number that is Integer
     * @return The rule
     */
    public IntegerTypeRule integerType()
    {
        return new IntegerTypeRule();
    }

    /**
     * Matches an integer greater than zero
     * @return The rule
     */
    public IntegerTypeRule positiveIntegerType(boolean includesZero, Long maxValue)
    {
        return new IntegerTypeRule(Range.closed(includesZero ? 0L : 1L, maxValue));
    }

    /**
     * Matches any type of number
     * @return The rule
     */
    public Rule numberType()
    {
        return new NumberTypeRule();
    }

    /**
     * Matches any number greater than zero
     * @return The rule
     */
    public Rule positiveNumberType()
    {
        return new NumberTypeRule(Range.greaterThan(0D));
    }

    /**
     * Matches a number that is Integer and is included in the range
     * @return The rule
     */
    public RangeValueRule range(Integer min, Integer max)
    {
        return new RangeValueRule(new IntRange(min, max));
    }

    /**
     * Matches a number that is Integer and its value it the specified
     * @param value The value to match
     * @return The rule
     */
    public IntegerValueRule integer(Integer value)
    {
        return new IntegerValueRule(new BigInteger(value.toString()));
    }

    /**
     * Matches a field that the key matches the key rule and the value the value rule
     * @param keyRule The key rule
     * @param valueRule The value rule
     * @return The rule
     */
    public KeyValueRule field(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, optional(valueRule));
    }

    /**
     * Matches a field that the key is of string type and matches the specified key name and the value matches the value rule or null value
     * @param keyName The key name
     * @param valueRule The value rule
     * @return The rule
     */
    public KeyValueRule field(String keyName, Rule valueRule)
    {
        return new KeyValueRule(string(keyName), optional(valueRule));
    }

    /**
     * Matches a field that the key is of string type and matches the specified key name and the value matches the value rule.
     * The difference with the {@link this#field(String, Rule)} it that Null is not matched for the value.
     * @param keyRule The key rule
     * @param valueRule The value rule
     * @return The rule
     */
    public KeyValueRule fieldWithRequiredValue(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, valueRule);
    }

    /**
     * Matches a field that the key is of string type and matches the specified key name and the value matches the value rule.
     * The difference with the {@link this#field(String, Rule)} it that Null is not matched for the value and also mark this field as required in the object rule.
     *
     * @param keyRule The key rule
     * @param valueRule The value rule
     * @return The rule
     */
    public KeyValueRule requiredField(Rule keyRule, Rule valueRule)
    {
        return new KeyValueRule(keyRule, valueRule).required();
    }

    /**
     * Matches any scalar value e.g Number String boolean etc
     * @return The rule
     */
    public Rule scalarType()
    {
        return new ScalarTypeRule();
    }

    /**
     * Matches any String type value
     * @return The rule
     */
    public StringTypeRule stringType()
    {
        return new StringTypeRule();
    }

    /**
     * Matches any Boolean type value
     * @return The rule
     */
    public BooleanTypeRule booleanType()
    {
        return new BooleanTypeRule();
    }

    /**
     * Matches any value of type string with the specified value
     * @param value The value to match
     * @return The rule
     */
    public StringValueRule string(String value)
    {
        return new StringValueRule(value);
    }

    /**
     * Matches any value that is accepted by the regex pattern
     * @param pattern The pattern
     * @return The rule
     */
    public RegexValueRule regex(String pattern)
    {
        return new RegexValueRule(Pattern.compile(pattern));
    }

    /**
     * Matches any value that is accepted by the regex pattern
     * @param pattern The pattern
     * @return The rule
     */
    public RegexValueRule regex(Pattern pattern)
    {
        return new RegexValueRule(pattern);
    }

    /**
     * Matches if any rule matches and suggests all the possibilities.
     * @param rules The option rules
     * @return The rule
     */
    public AnyOfRule anyOf(Rule... rules)
    {
        return new AnyOfRule(Arrays.asList(rules));
    }

    /**
     * Matches if any rule matches and suggests all the possibilities.
     * @param rules The option rules
     * @return The rule
     */
    public AnyOfRule anyOf(List<Rule> rules)
    {
        return new AnyOfRule(rules);
    }

    /**
     * Accepts if any rule matches and delegates the suggestion to the first one that matches.
     * @param rules The rules
     */
    public AnyOfRule firstOf(Rule... rules)
    {
        return new FirstOfRule(Arrays.asList(rules));
    }

    /**
     * Matches if the specified rule does not match
     * @param rule The rule to be negated
     * @return The rule
     */
    public NegativeRule not(Rule rule)
    {
        return new NegativeRule(rule);
    }

    /**
     * Matches if all the specified rules matches
     * @param rules All the rules to match
     * @return The rule
     */
    public AllOfRule allOf(Rule... rules)
    {
        return new AllOfRule(Arrays.asList(rules));
    }

    /**
     * Matches if the rule matches or the value is null
     * @param rule The rule to match
     * @return The rule
     */
    public AnyOfRule optional(Rule rule)
    {
        return anyOf(rule, nullValue());
    }

    /**
     * Matches a string that its length is bigger or equals to the specified
     * @param length The length
     * @return The rule
     */
    public MinLengthRule minLength(int length)
    {
        return new MinLengthRule(length);
    }


    /**
     * Matches a null value.
     *
     * @return The rule
     */
    protected NullValueRule nullValue()
    {
        return new NullValueRule();
    }

    /**
     * It will dispatch the to the conditional rule that matches the selected value. Similar to a pattern matching scenario
     * @param expr The expression to select
     * @param cases The conditional cases
     * @return The rule
     */
    public ConditionalRules when(String expr, ConditionalRule... cases)
    {
        return new ConditionalRules(singletonList(expr), cases);
    }

    /**
     * It will dispatch the to the conditional rule that matches the selected value. Similar to a pattern matching scenario
     * @param expr The list of expressions to try the first that is not null will be used
     * @param cases The conditional cases
     * @return The rule
     */
    public ConditionalRules when(List<String> expr, ConditionalRule... cases)
    {
        return new ConditionalRules(expr, cases);
    }

    /**
     * Conditional rule that will accept if the rule matches
     * @param rule The rule to be used as matching
     * @return The rule
     */
    public ConditionalRule is(Rule rule)
    {
        return new ConditionalRule(rule);
    }

    /**
     * Returns a default value that is the parent key
     * @return The default value
     */
    public DefaultValue parentKey()
    {
        return new ParentKeyDefaultValue();
    }

    /**
     * Creates a new set of exclusive rules
     * @param keys Each of the mutually exclusive rules
     * @return The rule
     */
    public ExclusiveKeys exclusiveKeys(String... keys)
    {
        List<Rule> rules = new ArrayList<>();
        for (String key : keys)
        {
            rules.add(string(key));
        }
        return new ExclusiveKeys(rules);
    }
}
