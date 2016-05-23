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
package org.raml.v2.internal.impl.commons.type;

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
import org.raml.v2.internal.impl.v10.type.ArrayTypeDefinition;
import org.raml.v2.internal.impl.v10.type.BooleanTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateOnlyTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateTimeOnlyTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateTimeTypeDefinition;
import org.raml.v2.internal.impl.v10.type.FileTypeDefinition;
import org.raml.v2.internal.impl.v10.type.IntegerTypeDefinition;
import org.raml.v2.internal.impl.v10.type.NullTypeDefinition;
import org.raml.v2.internal.impl.v10.type.NumberTypeDefinition;
import org.raml.v2.internal.impl.v10.type.ObjectPropertyDefinition;
import org.raml.v2.internal.impl.v10.type.ObjectTypeDefinition;
import org.raml.v2.internal.impl.v10.type.StringTypeDefinition;
import org.raml.v2.internal.impl.v10.type.TimeOnlyTypeDefinition;
import org.raml.v2.internal.impl.v10.type.TypeDefinitionVisitor;
import org.raml.v2.internal.impl.v10.type.UnionTypeDefinition;
import org.raml.v2.internal.utils.DateType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.raml.v2.internal.utils.BasicRuleFactory.keyValue;
import static org.raml.v2.internal.utils.BasicRuleFactory.stringValue;

public class TypeToRuleVisitor implements TypeDefinitionVisitor<Rule>
{

    private ResourceLoader resourceLoader;
    private boolean strictMode = false;

    public TypeToRuleVisitor(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public Rule generateRule(TypeDefinition items, boolean strict)
    {
        this.strictMode = strict;
        return items.visit(this);
    }

    public Rule generateRule(TypeDefinition items)
    {
        return generateRule(items, strictMode);
    }


    @Override
    public Rule visitString(StringTypeDefinition stringTypeNode)
    {
        final AllOfRule typeRule = new AllOfRule(new StringTypeRule());
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
    public Rule visitObject(ObjectTypeDefinition objectTypeDefinition)
    {
        final ObjectRule objectRule = new ObjectRule(strictMode);
        objectRule.additionalProperties(asBoolean(objectTypeDefinition.getAdditionalProperties(), true));
        final Map<String, ObjectPropertyDefinition> properties = objectTypeDefinition.getProperties();
        for (Map.Entry<String, ObjectPropertyDefinition> property : properties.entrySet())
        {
            final ObjectPropertyDefinition propertyValue = property.getValue();
            final KeyValueRule keyValue = keyValue(property.getKey(), generateRule(propertyValue.getTypeDefinition()));
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

    protected boolean asBoolean(Boolean required, boolean defaultValue)
    {
        return required == null ? defaultValue : required;
    }

    @Override
    public Rule visitBoolean(BooleanTypeDefinition booleanTypeDefinition)
    {
        return new BooleanTypeRule();
    }

    @Override
    public Rule visitInteger(IntegerTypeDefinition integerTypeDefinition)
    {
        return visitNumber(integerTypeDefinition, new IntegerTypeRule());
    }

    @Override
    public Rule visitNumber(NumberTypeDefinition numberTypeDefinition)
    {
        return visitNumber(numberTypeDefinition, new NumberTypeRule());
    }

    private Rule visitNumber(NumberTypeDefinition numericTypeNode, Rule numericTypeRule)
    {
        final AllOfRule typeRule = new AllOfRule(numericTypeRule);

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
    public Rule visitDateTimeOnly(DateTimeOnlyTypeDefinition dateTimeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.datetime_only, null);
    }

    @Override
    public Rule visitDate(DateOnlyTypeDefinition dateOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.date_only, null);
    }

    @Override
    public Rule visitDateTime(DateTimeTypeDefinition dateTimeTypeDefinition)
    {
        return new DateValueRule(DateType.datetime, dateTimeTypeDefinition.getFormat());
    }

    @Override
    public Rule visitTimeOnly(TimeOnlyTypeDefinition timeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.time_only, null);
    }

    @Override
    public Rule visitJson(JsonSchemaTypeDefinition jsonTypeDefinition)
    {
        return new JsonSchemaValidationRule(jsonTypeDefinition);
    }

    @Override
    public Rule visitXml(XmlSchemaTypeDefinition xmlTypeDefinition)
    {
        return new XmlSchemaValidationRule(xmlTypeDefinition, resourceLoader);
    }

    @Override
    public Rule visitFile(FileTypeDefinition fileTypeDefinition)
    {
        // TODO how do we validate files??
        return new AnyValueRule();
    }

    @Override
    public Rule visitNull(NullTypeDefinition nullTypeDefinition)
    {
        return new NullValueRule();
    }

    @Override
    public Rule visitArray(ArrayTypeDefinition arrayTypeDefinition)
    {
        final TypeDefinition items = arrayTypeDefinition.getItems();
        final AllOfRule rule = new AllOfRule(new ArrayRule(generateRule(items)));

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
    public Rule visitUnion(UnionTypeDefinition unionTypeDefinition)
    {
        final List<TypeDefinition> of = unionTypeDefinition.of();
        final List<Rule> rules = new ArrayList<>();
        for (TypeDefinition typeDefinition : of)
        {
            rules.add(generateRule(typeDefinition, true));
        }
        return new AnyOfRule(rules);
    }

}
