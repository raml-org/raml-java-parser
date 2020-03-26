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

import com.google.common.base.Predicate;
import org.apache.commons.lang.math.NumberRange;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.JsonSchemaValidationRule;
import org.raml.v2.internal.impl.commons.rule.XmlSchemaValidationRule;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.impl.v10.rules.DiscriminatorBasedRule;
import org.raml.v2.internal.impl.v10.rules.FormatValueRule;
import org.raml.yagi.framework.grammar.rule.*;
import org.raml.yagi.framework.util.DateType;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.filterEntries;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.raml.v2.internal.utils.BasicRuleFactory.property;
import static org.raml.v2.internal.utils.BasicRuleFactory.regexValue;
import static org.raml.v2.internal.utils.BasicRuleFactory.stringValue;
import static org.raml.v2.internal.utils.ValueUtils.asBoolean;

public class TypeToRuleVisitor implements TypeVisitor<Rule>
{

    private static final String CAST_STRINGS_AS_NUMBERS_PROP = "org.raml.cast_strings_as_numbers";
    public static boolean CAST_STRINGS_AS_NUMBERS = Boolean.parseBoolean(System.getProperty(CAST_STRINGS_AS_NUMBERS_PROP, "false"));
    private static final String NILLABLE_STRINGS_PROP = "org.raml.nillable_strings";
    public static boolean NILLABLE_STRINGS = Boolean.parseBoolean(System.getProperty(NILLABLE_STRINGS_PROP, "false"));

    private ResourceLoader resourceLoader;
    private final boolean useDiscriminatorsToCalculateTypes;
    private boolean strictMode = false;
    private Map<ResolvedType, Rule> definitionRuleMap = new IdentityHashMap<>();

    // Flag that should be turn on when discriminator should be resolved
    private boolean resolvingDiscriminator = false;

    public TypeToRuleVisitor(ResourceLoader resourceLoader, boolean useDiscriminatorsToCalculateTypes)
    {
        this.resourceLoader = resourceLoader;
        this.useDiscriminatorsToCalculateTypes = useDiscriminatorsToCalculateTypes;
    }

    public TypeToRuleVisitor(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
        this.useDiscriminatorsToCalculateTypes = true;
    }

    public Rule generateRule(ResolvedType items)
    {
        if (definitionRuleMap.containsKey(items))
        {
            return definitionRuleMap.get(items);
        }
        else
        {
            return items.visit(this);
        }
    }


    @Override
    public Rule visitString(StringResolvedType stringTypeNode)
    {
        final AllOfRule typeRule = new AllOfRule(new StringTypeRule(NILLABLE_STRINGS));
        registerRule(stringTypeNode, typeRule);
        if (isNotEmpty(stringTypeNode.getPattern()))
        {
            typeRule.and(new RegexValueRule(Pattern.compile(stringTypeNode.getPattern()), NILLABLE_STRINGS));
        }

        if (stringTypeNode.getEnums() != null && !stringTypeNode.getEnums().isEmpty())
        {
            typeRule.and(new AnyOfRule(new ArrayList<>(getStringRules(stringTypeNode.getEnums()))));
        }

        if (stringTypeNode.getMaxLength() != null)
        {
            Integer maxLength = stringTypeNode.getMaxLength();
            typeRule.and(new MaxLengthRule(maxLength, NILLABLE_STRINGS));
        }

        if (stringTypeNode.getMinLength() != null)
        {
            Integer maxLength = stringTypeNode.getMinLength();
            typeRule.and(new MinLengthRule(maxLength, NILLABLE_STRINGS));
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

    public void resolveDiscrimintor()
    {
        this.resolvingDiscriminator = true;
    }

    @Override
    public Rule visitObject(final ObjectResolvedType objectTypeDefinition)
    {
        if (useDiscriminatorsToCalculateTypes == false)
        {

            final ObjectRule objectRule = new ObjectRule(strictMode);
            registerRule(objectTypeDefinition, objectRule);

            final boolean isAdditionalPropertiesEnabled = asBoolean(objectTypeDefinition.getAdditionalProperties(), true);
            objectRule.additionalProperties(isAdditionalPropertiesEnabled);

            final Map<String, PropertyFacets> properties = objectTypeDefinition.getProperties();
            final Map<String, PropertyFacets> nonPatternProperties = filterEntries(getNonPatternProperties(properties), getAllButDiscriminator(objectTypeDefinition));
            addFieldsToRule(objectRule, nonPatternProperties);

            // If additional properties is set to false the pattern properties are ignored
            if (isAdditionalPropertiesEnabled)
            {
                // Additional properties should be processed after specified properties, so they will be added at the end
                final Map<String, PropertyFacets> additionalProperties = getPatternProperties(properties);
                addAdditionalPropertiesToRule(objectRule, additionalProperties);
            }

            if (isNotEmpty(objectTypeDefinition.getDiscriminator()))
            {

                objectRule.with(new KeyValueRule(new StringValueRule(objectTypeDefinition.getDiscriminator()), new StringTypeRule()).required()).discriminatorName(
                        objectTypeDefinition.getDiscriminator());
            }

            final AllOfRule allOfRule = new AllOfRule(objectRule);

            if (objectTypeDefinition.getMaxProperties() != null)
            {
                allOfRule.and(new MaxPropertiesRule(objectTypeDefinition.getMaxProperties()));
            }

            if (objectTypeDefinition.getMinProperties() != null)
            {
                allOfRule.and(new MinPropertiesRule(objectTypeDefinition.getMinProperties()));
            }

            return allOfRule;
        }

        if (!resolvingDiscriminator && isNotEmpty(objectTypeDefinition.getDiscriminator()))
        {
            resolvingDiscriminator = false;
            final TypeExpressionNode typeDeclarationNode = objectTypeDefinition.getTypeExpressionNode();
            return new DiscriminatorBasedRule(this, typeDeclarationNode.getRootNode(), objectTypeDefinition.getDiscriminator());
        }
        else
        {

            final ObjectRule objectRule = new ObjectRule(strictMode);
            registerRule(objectTypeDefinition, objectRule);

            final boolean isAdditionalPropertiesEnabled = asBoolean(objectTypeDefinition.getAdditionalProperties(), true);
            objectRule.additionalProperties(isAdditionalPropertiesEnabled);

            final Map<String, PropertyFacets> properties = objectTypeDefinition.getProperties();

            final Map<String, PropertyFacets> nonPatternProperties = getNonPatternProperties(properties);
            addFieldsToRule(objectRule, nonPatternProperties);

            // If additional properties is set to false the pattern properties are ignored
            if (isAdditionalPropertiesEnabled)
            {
                // Additional properties should be processed after specified properties, so they will be added at the end
                final Map<String, PropertyFacets> additionalProperties = getPatternProperties(properties);
                addAdditionalPropertiesToRule(objectRule, additionalProperties);
            }

            /*
             * if (isNotEmpty(objectTypeDefinition.getDiscriminator())) {
             * 
             * StringTypeRule value = new StringTypeRule(); objectRule.with(property(objectTypeDefinition.getDiscriminator(), value).); }
             */

            final AllOfRule allOfRule = new AllOfRule(objectRule);

            if (objectTypeDefinition.getMaxProperties() != null)
            {
                allOfRule.and(new MaxPropertiesRule(objectTypeDefinition.getMaxProperties()));
            }

            if (objectTypeDefinition.getMinProperties() != null)
            {
                allOfRule.and(new MinPropertiesRule(objectTypeDefinition.getMinProperties()));
            }

            return allOfRule;
        }
    }

    private static Predicate<Entry<String, PropertyFacets>> getAllButDiscriminator(final ObjectResolvedType objectTypeDefinition)
    {
        return new Predicate<Entry<String, PropertyFacets>>()
        {
            @Override
            public boolean apply(@Nullable Entry<String, PropertyFacets> input)
            {
                if (input != null && isNotEmpty(objectTypeDefinition.getDiscriminator()))
                {
                    return !input.getKey().equals(objectTypeDefinition.getDiscriminator());
                }
                else
                {

                    return true;
                }
            }
        };
    }

    private void addFieldsToRule(ObjectRule objectRule, Map<String, PropertyFacets> properties)
    {
        for (Entry<String, PropertyFacets> property : properties.entrySet())
        {
            final PropertyFacets propertyValue = property.getValue();
            final Rule value = generateRule(propertyValue.getValueType());

            final KeyValueRule keyValue = property(property.getKey(), value);
            final Boolean required = propertyValue.isRequired();
            if (required)
            {
                keyValue.required();
            }
            objectRule.with(keyValue);
        }
    }

    private void addAdditionalPropertiesToRule(ObjectRule objectRule, Map<String, PropertyFacets> properties)
    {
        for (PropertyFacets property : properties.values())
        {
            final Rule value = generateRule(property.getValueType());
            final KeyValueRule keyValue = new KeyValueRule(regexValue(property.getPatternRegex()).fullMatch(false), value);
            // We set to false as it should only validate the ones that matches the regex
            objectRule.additionalProperties(false);
            objectRule.with(keyValue);
        }
    }

    private Map<String, PropertyFacets> getPatternProperties(Map<String, PropertyFacets> properties)
    {
        return filterEntries(properties, isPatternProperty());
    }

    private Map<String, PropertyFacets> getNonPatternProperties(Map<String, PropertyFacets> properties)
    {
        return filterEntries(properties, not(isPatternProperty()));
    }

    private Predicate<Entry<String, PropertyFacets>> isPatternProperty()
    {
        return new Predicate<Entry<String, PropertyFacets>>()
        {
            @Override
            public boolean apply(Entry<String, PropertyFacets> entry)
            {
                return entry.getValue().isPatternProperty();
            }
        };
    }

    protected void registerRule(ResolvedType objectResolvedType, Rule objectRule)
    {
        definitionRuleMap.put(objectResolvedType, objectRule);
    }


    @Override
    public Rule visitBoolean(BooleanResolvedType booleanTypeDefinition)
    {
        return new BooleanTypeRule();
    }

    @Override
    public Rule visitInteger(IntegerResolvedType integerTypeDefinition)
    {
        return visitNumber(integerTypeDefinition, new IntegerTypeRule(CAST_STRINGS_AS_NUMBERS));
    }

    @Override
    public Rule visitNumber(NumberResolvedType numberTypeDefinition)
    {
        return visitNumber(numberTypeDefinition, new NumberTypeRule(CAST_STRINGS_AS_NUMBERS));
    }

    private Rule visitNumber(NumberResolvedType numericTypeNode, Rule numericTypeRule)
    {
        final AllOfRule typeRule = new AllOfRule(numericTypeRule);
        registerRule(numericTypeNode, typeRule);
        if (numericTypeNode.getMinimum() != null && numericTypeNode.getMaximum() != null)
        {
            if (numericTypeNode.getMinimum().getClass() == numericTypeNode.getMaximum().getClass())
            {

                typeRule.and(new RangeValueRule(new NumberRange(numericTypeNode.getMinimum(), numericTypeNode.getMaximum())));
            }
            else
            {

                typeRule.and(new RangeValueRule(new NumberRange(numericTypeNode.getMinimum().doubleValue(), numericTypeNode.getMaximum().doubleValue())));
            }
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
            typeRule.and(new DivisorValueRule(numericTypeNode.getMultiple(), CAST_STRINGS_AS_NUMBERS));
        }
        if (numericTypeNode.getFormat() != null)
        {
            typeRule.and(new FormatValueRule(numericTypeNode.getFormat()));
        }

        final List<Number> enums = numericTypeNode.getEnums();
        if (enums != null && !enums.isEmpty())
        {
            final List<Rule> options = new ArrayList<>();
            for (Number anEnum : enums)
            {
                options.add(new NumberValueRule(anEnum));
            }
            typeRule.and(new AnyOfRule(options));
        }
        return typeRule;
    }

    @Override
    public Rule visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.datetime_only, null);
    }

    @Override
    public Rule visitDate(DateOnlyResolvedType dateOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.date_only, null);
    }

    @Override
    public Rule visitDateTime(DateTimeResolvedType dateTimeTypeDefinition)
    {
        return new DateValueRule(DateType.datetime, dateTimeTypeDefinition.getFormat());
    }

    @Override
    public Rule visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition)
    {
        return new DateValueRule(DateType.time_only, null);
    }

    @Override
    public Rule visitJson(JsonSchemaExternalType jsonTypeDefinition)
    {
        return new JsonSchemaValidationRule(jsonTypeDefinition);
    }

    @Override
    public Rule visitXml(XmlSchemaExternalType xmlTypeDefinition)
    {
        return new XmlSchemaValidationRule(xmlTypeDefinition, resourceLoader);
    }

    @Override
    public Rule visitAny(AnyResolvedType anyResolvedType)
    {
        return new AnyValueRule();
    }

    @Override
    public Rule visitFile(FileResolvedType fileTypeDefinition)
    {
        // TODO how do we validate files??
        return new AnyValueRule();
    }

    @Override
    public Rule visitNull(NullResolvedType nullTypeDefinition)
    {
        return new NullValueRule();
    }

    @Override
    public Rule visitArray(ArrayResolvedType arrayTypeDefinition)
    {
        final ResolvedType items = arrayTypeDefinition.getItems();
        final AllOfRule rule = new AllOfRule(new ArrayRule(generateRule(items), strictMode));
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
    public Rule visitUnion(UnionResolvedType unionTypeDefinition)
    {
        final List<ResolvedType> of = unionTypeDefinition.of();
        final List<Rule> rules = new ArrayList<>();

        boolean oldStrictMode = strictMode;
        strictMode = true;
        for (ResolvedType resolvedType : of)
        {

            rules.add(generateRule(resolvedType));
        }
        strictMode = oldStrictMode;

        return new AnyOfRule(rules);
    }

}
