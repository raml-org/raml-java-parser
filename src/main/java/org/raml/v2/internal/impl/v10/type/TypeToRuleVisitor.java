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
package org.raml.v2.internal.impl.v10.type;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.AllOfRule;
import org.raml.v2.internal.framework.grammar.rule.AnyOfRule;
import org.raml.v2.internal.framework.grammar.rule.AnyValueRule;
import org.raml.v2.internal.framework.grammar.rule.ArrayRule;
import org.raml.v2.internal.framework.grammar.rule.BooleanTypeRule;
import org.raml.v2.internal.framework.grammar.rule.DateValueRule;
import org.raml.v2.internal.framework.grammar.rule.DivisorValueRule;
import org.raml.v2.internal.framework.grammar.rule.IntegerTypeRule;
import org.raml.v2.internal.framework.grammar.rule.KeyValueRule;
import org.raml.v2.internal.framework.grammar.rule.MaxItemsRule;
import org.raml.v2.internal.framework.grammar.rule.MaxLengthRule;
import org.raml.v2.internal.framework.grammar.rule.MaxPropertiesRule;
import org.raml.v2.internal.framework.grammar.rule.MaximumValueRule;
import org.raml.v2.internal.framework.grammar.rule.MinItemsRule;
import org.raml.v2.internal.framework.grammar.rule.MinLengthRule;
import org.raml.v2.internal.framework.grammar.rule.MinimumValueRule;
import org.raml.v2.internal.framework.grammar.rule.NullValueRule;
import org.raml.v2.internal.framework.grammar.rule.NumberTypeRule;
import org.raml.v2.internal.framework.grammar.rule.ObjectRule;
import org.raml.v2.internal.framework.grammar.rule.RangeValueRule;
import org.raml.v2.internal.framework.grammar.rule.RegexValueRule;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.grammar.rule.StringTypeRule;
import org.raml.v2.internal.impl.commons.rule.JsonSchemaValidationRule;
import org.raml.v2.internal.impl.commons.rule.XmlSchemaValidationRule;
import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeFacets;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeFacets;
import org.raml.v2.internal.utils.DateType;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.raml.v2.internal.utils.BasicRuleFactory.keyValue;
import static org.raml.v2.internal.utils.BasicRuleFactory.stringValue;

public class TypeToRuleVisitor implements TypeFacetsVisitor<Rule>
{

    private ResourceLoader resourceLoader;
    private boolean strictMode = false;
    private Map<TypeFacets, Rule> definitionRuleMap = new IdentityHashMap<>();

    public TypeToRuleVisitor(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public Rule generateRule(TypeFacets items, boolean strict)
    {
        if (definitionRuleMap.containsKey(items))
        {
            return definitionRuleMap.get(items);
        }
        else
        {
            this.strictMode = strict;
            return items.visit(this);
        }
    }

    public Rule generateRule(TypeFacets items)
    {
        return generateRule(items, strictMode);
    }

    @Override
    public Rule visitString(StringTypeFacets stringTypeNode)
    {
        final AllOfRule typeRule = new AllOfRule(new StringTypeRule());
        registerRule(stringTypeNode, typeRule);
        if (StringUtils.isNotEmpty(stringTypeNode.getPattern()))
        {
            typeRule.and(new RegexValueRule(Pattern.compile(stringTypeNode.getPattern())));
        }

        if (stringTypeNode.getEnums() != null && !stringTypeNode.getEnums().isEmpty())
        {
            typeRule.and(new AnyOfRule(new ArrayList<>(getStringRules(stringTypeNode.getEnums()))));
        }

        if (stringTypeNode.getMaxLength() != null)
        {
            Integer maxLength = stringTypeNode.getMaxLength();
            typeRule.and(new MaxLengthRule(maxLength));
        }

        if (stringTypeNode.getMinLength() != null)
        {
            Integer maxLength = stringTypeNode.getMinLength();
            typeRule.and(new MinLengthRule(maxLength));
        }
        return typeRule;
    }

    private List<Rule> getStringRules(List<String> enumValues)
    {
        final List<Rule> rules = new ArrayList<>();
        for (String value : enumValues)
        {
            rules.add(stringValue(value));
        }
        return rules;
    }

    @Override
    public Rule visitObject(ObjectTypeFacets objectTypeDefinition)
    {
        final ObjectRule objectRule = new ObjectRule(strictMode);
        registerRule(objectTypeDefinition, objectRule);
        objectRule.additionalProperties(asBoolean(objectTypeDefinition.getAdditionalProperties(), true));
        final Map<String, ObjectPropertyDefinition> properties = objectTypeDefinition.getProperties();
        for (Map.Entry<String, ObjectPropertyDefinition> property : properties.entrySet())
        {
            final ObjectPropertyDefinition propertyValue = property.getValue();
            final KeyValueRule keyValue = keyValue(property.getKey(), generateRule(propertyValue.getTypeFacets()));
            final Boolean required = propertyValue.getRequired();
            if (required)
            {
                keyValue.required();
            }
            objectRule.with(keyValue);
        }

        final AllOfRule allOfRule = new AllOfRule(objectRule);

        if (objectTypeDefinition.getMaxProperties() != null)
        {
            allOfRule.and(new MaxPropertiesRule(objectTypeDefinition.getMaxProperties()));
        }

        if (objectTypeDefinition.getMinProperties() != null)
        {
            allOfRule.and(new MaxPropertiesRule(objectTypeDefinition.getMinProperties()));
        }

        return allOfRule;
    }

    protected void registerRule(TypeFacets objectTypeFacets, Rule objectRule)
    {
        definitionRuleMap.put(objectTypeFacets, objectRule);
    }

    protected boolean asBoolean(Boolean required, boolean defaultValue)
    {
        return required == null ? defaultValue : required;
    }

    @Override
    public Rule visitBoolean(BooleanTypeFacets booleanTypeDefinition)
    {
        return new BooleanTypeRule();
    }

    @Override
    public Rule visitInteger(IntegerTypeFacets integerTypeDefinition)
    {
        return visitNumber(integerTypeDefinition, new IntegerTypeRule());
    }

    @Override
    public Rule visitNumber(NumberTypeFacets numberTypeDefinition)
    {
        return visitNumber(numberTypeDefinition, new NumberTypeRule());
    }

    private Rule visitNumber(NumberTypeFacets numericTypeNode, Rule numericTypeRule)
    {
        final AllOfRule typeRule = new AllOfRule(numericTypeRule);
        registerRule(numericTypeNode, typeRule);
        if (numericTypeNode.getMinimum() != null && numericTypeNode.getMaximum() != null)
        {
            typeRule.and(new RangeValueRule(numericTypeNode.getMinimum(), numericTypeNode.getMaximum()));
        }
        else if (numericTypeNode.getMinimum() != null)
        {
            typeRule.and(new MinimumValueRule(numericTypeNode.getMinimum()));
        }
        else if (numericTypeNode.getMaximum() != null)
        {
            typeRule.and(new MaximumValueRule(numericTypeNode.getMaximum()));
        }
        if (numericTypeNode.getMultiple() != null)
        {
            typeRule.and(new DivisorValueRule(numericTypeNode.getMultiple()));
        }
        return typeRule;
    }

    @Override
    public Rule visitDateTimeOnly(DateTimeOnlyTypeFacets dateTimeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.datetime_only, null);
    }

    @Override
    public Rule visitDate(DateOnlyTypeFacets dateOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.date_only, null);
    }

    @Override
    public Rule visitDateTime(DateTimeTypeFacets dateTimeTypeDefinition)
    {
        return new DateValueRule(DateType.datetime, dateTimeTypeDefinition.getFormat());
    }

    @Override
    public Rule visitTimeOnly(TimeOnlyTypeFacets timeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.time_only, null);
    }

    @Override
    public Rule visitJson(JsonSchemaTypeFacets jsonTypeDefinition)
    {
        return new JsonSchemaValidationRule(jsonTypeDefinition);
    }

    @Override
    public Rule visitXml(XmlSchemaTypeFacets xmlTypeDefinition)
    {
        return new XmlSchemaValidationRule(xmlTypeDefinition, resourceLoader);
    }

    @Override
    public Rule visitFile(FileTypeFacets fileTypeDefinition)
    {
        // TODO how do we validate files??
        return new AnyValueRule();
    }

    @Override
    public Rule visitNull(NullTypeFacets nullTypeDefinition)
    {
        return new NullValueRule();
    }

    @Override
    public Rule visitArray(ArrayTypeFacets arrayTypeDefinition)
    {
        final TypeFacets items = arrayTypeDefinition.getItems();
        final AllOfRule rule = new AllOfRule(new ArrayRule(generateRule(items)));
        registerRule(arrayTypeDefinition, rule);
        if (arrayTypeDefinition.getMaxItems() != null)
        {
            rule.and(new MaxItemsRule(arrayTypeDefinition.getMaxItems()));
        }

        if (arrayTypeDefinition.getMinItems() != null)
        {
            rule.and(new MinItemsRule(arrayTypeDefinition.getMinItems()));
        }

        // TODO uniques how do we compare values?

        return rule;
    }


    @Override
    public Rule visitUnion(UnionTypeFacets unionTypeDefinition)
    {
        final List<TypeFacets> of = unionTypeDefinition.of();
        final List<Rule> rules = new ArrayList<>();
        for (TypeFacets typeFacets : of)
        {
            rules.add(generateRule(typeFacets, true));
        }
        return new AnyOfRule(rules);
    }

}
