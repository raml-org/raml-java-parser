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
package org.raml.v2.internal.impl.v10.grammar;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.yagi.framework.grammar.RuleFactory;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.grammar.rule.ArrayWrapperFactory;
import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.v2.internal.impl.commons.rule.NodeReferenceFactory;
import org.raml.yagi.framework.grammar.rule.ObjectRule;
import org.raml.yagi.framework.grammar.rule.RegexValueRule;
import org.raml.yagi.framework.grammar.rule.ResourceRefRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;
import org.raml.yagi.framework.nodes.NullNodeImpl;
import org.raml.v2.internal.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.internal.impl.commons.nodes.AnnotationNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationTypeNode;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.ExtendsNode;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.MethodNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.rule.SchemaDeclarationRule;
import org.raml.v2.internal.impl.v10.nodes.DisplayNameDefaultValue;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.nodes.factory.DefaultMimeTypeDeclarationFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.InlineTypeDeclarationFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.OverlayableSimpleTypeFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.RamlScalarValueFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.TypeExpressionReferenceFactory;
import org.raml.v2.internal.impl.v10.rules.TypeDefaultValue;
import org.raml.v2.internal.impl.v10.rules.TypeExpressionReferenceRule;
import org.raml.v2.internal.impl.v10.type.TypeId;


public class Raml10Grammar extends BaseRamlGrammar
{

    public static final String ANNOTATION_TYPES_KEY_NAME = "annotationTypes";
    public static final String DEFAULT_TYPE_RULE = "defaultTypeRule";
    public static final String PROPERTY_TYPE_RULE = "propertyTypeRule";
    public static final String TYPES_FACET_TYPE = "type";
    public static final String TYPES_FACET_SCHEMA = "schema";

    public ObjectRule untitledRaml()
    {
        return super.untitledRaml()
                    .with(annotationTypesField())
                    .with(annotationField())
                    .with(typesField())
                    .with(usesField())
                    .with(exclusiveKeys(TYPES_KEY_NAME, SCHEMAS_KEY_NAME));
    }

    @Override
    protected ObjectRule resourceValue()
    {
        return named("resourceValue", new RuleFactory<ObjectRule>()
        {
            @Override
            public ObjectRule create()
            {
                return baseResourceValue()
                                          .with(field(anyMethod(), methodValue()).then(MethodNode.class))
                                          .with(field(resourceKey(), resourceValue()).then(ResourceNode.class))
                                          .with(annotationField());
            }
        });

    }

    @Override
    protected ObjectRule methodValue()
    {
        return super.methodValue()
                    .with(field(queryStringKey(), type()))
                    .with(exclusiveKeys(QUERY_STRING_KEY_NAME, QUERY_PARAMETERS_KEY_NAME))
                    .with(annotationField());
    }

    @Override
    protected ObjectRule securityScheme()
    {
        return super.securityScheme().with(annotationField());
    }

    @Override
    protected ObjectRule securitySchemePart()
    {
        return super.securitySchemePart()
                    .with(annotationField())
                    .with(exclusiveKeys(QUERY_STRING_KEY_NAME, QUERY_PARAMETERS_KEY_NAME));
    }

    @Override
    protected ObjectRule securitySchemeSettings()
    {
        return super.securitySchemeSettings()
                    .with(field(string("signatures"), array(scalarType())))
                    .with(field(string("authorizationUri"), ramlScalarValue()).requiredWhen(new AuthorizationUriRequiredField()))
                    .with(annotationField());
    }

    @Override
    protected ObjectRule response()
    {
        return super.response().with(annotationField());
    }

    protected KeyValueRule typesField()
    {
        return field(typesKey(), types());
    }

    protected StringValueRule typesKey()
    {
        return string(TYPES_KEY_NAME)
                                     .description("Declarations of (data) types for use within this API.");
    }


    // Common fields between rules
    protected KeyValueRule annotationField()
    {
        return field(annotationKey().then(new NodeReferenceFactory(AnnotationReferenceNode.class)), any()).then(AnnotationNode.class);
    }

    protected RegexValueRule annotationKey()
    {
        return regex("\\((.+)\\)")
                                  .label("(Annotation)")
                                  .suggest("(<cursor>)")
                                  .description("Annotations to be applied to this API. " +
                                               "Annotations are any property whose key begins with \"(\" and ends with \")\" " +
                                               "and whose name (the part between the beginning and ending parentheses) " +
                                               "is a declared annotation name..");
    }


    public KeyValueRule usesField()
    {
        return field(usesKey(), objectType().with(field(scalarType(), libraryRef()).then(LibraryNode.class)));
    }

    // Extension
    public ObjectRule extension()
    {
        return untitledRaml()
                             .with(requiredField(extendsKey(), scalarType()).then(ExtendsNode.class))
                             .with(usageField())
                             .with(optionalTitleField());
    }

    protected StringValueRule extendsKey()
    {
        return string("extends").description("The path to the base RAML document to be extended.");
    }

    protected KeyValueRule optionalTitleField()
    {
        return field(titleKey(), titleValue());
    }


    public Rule libraryRef()
    {
        return new ResourceRefRule().then(LibraryLinkNode.class);
    }

    public ObjectRule libraryValue()
    {
        return named("library", new RuleFactory<ObjectRule>()
        {
            @Override
            public ObjectRule create()
            {
                return objectType()
                                   .with(typesField())
                                   .with(schemasField())
                                   .with(resourceTypesField())
                                   .with(traitsField())
                                   .with(securitySchemesField())
                                   .with(annotationTypesField())
                                   .with(annotationField())
                                   .with(usesField())
                                   .with(usageField());
            }
        });
    }

    protected KeyValueRule annotationTypesField()
    {
        return field(annotationTypesKey(), annotationTypes());
    }

    protected StringValueRule annotationTypesKey()
    {
        return string(ANNOTATION_TYPES_KEY_NAME).description("Declarations of annotation types for use by annotations.");
    }

    protected Rule annotationTypes()
    {
        return objectType()
                           .with(field(scalarType(), annotationType()).then(AnnotationTypeNode.class));
    }

    private Rule annotationType()
    {
        return anyOf(inlineType(), explicitType().with(allowedTargetsField()));
    }

    private KeyValueRule allowedTargetsField()
    {
        return field(string("allowedTargets"), anyOf(scalarType(), array(scalarType())).then(new ArrayWrapperFactory()));
    }


    protected Rule types()
    {
        return objectType()
                           .with(field(scalarType(), type()).then(TypeDeclarationField.class));
    }

    protected Rule parameter()
    {
        return anyOf(inlineType(), propertyType());
    }

    public Rule type()
    {
        return anyOf(inlineType(), explicitType());
    }

    private AnyOfRule typeRef()
    {
        return anyOf(inlineType(), explicitType());
    }

    protected Rule inlineType()
    {
        return typeExpressionReference().then(new InlineTypeDeclarationFactory());
    }

    public ObjectRule explicitType()
    {
        return baseType(TypeId.STRING, DEFAULT_TYPE_RULE);
    }

    private ObjectRule baseType(final TypeId defaultType, final String ruleName, final KeyValueRule... additionalRules)
    {
        return named(ruleName,
                new RuleFactory<ObjectRule>()
                {
                    @Override
                    public ObjectRule create()
                    {
                        return objectType()
                                           .with(typeField(defaultType))
                                           .with(xmlFacetField())
                                           .with(displayNameField())
                                           .with(descriptionField())
                                           .with(usageField())
                                           .with(annotationField())
                                           .with(defaultField())
                                           .with(requiredField())
                                           .with(facetsField())
                                           .with(exampleField())
                                           .with(examplesField())
                                           .with(exclusiveKeys(TYPES_FACET_TYPE, TYPES_FACET_SCHEMA))
                                           .with(
                                                   when(asList(TYPES_FACET_TYPE, TYPES_FACET_SCHEMA),
                                                           is(stringTypeLiteral())
                                                                                  .add(patternField())
                                                                                  .add(minLengthField())
                                                                                  .add(maxLengthField())
                                                                                  .add(enumField()),
                                                           is(dateTimeTypeLiteral())
                                                                                    .add(formatField()),
                                                           is(arrayTypeLiteral())
                                                                                 .add(uniqueItemsField())
                                                                                 .add(itemsField())
                                                                                 .add(minItemsField())
                                                                                 .add(maxItemsField()),
                                                           is(numericTypeLiteral())
                                                                                   .add(minimumField())
                                                                                   .add(maximumField())
                                                                                   .add(numberFormat())
                                                                                   .add(enumField())
                                                                                   .add(multipleOfField()),
                                                           is(fileTypeLiteral())
                                                                                .add(fileTypesField())
                                                                                .add(minLengthField())
                                                                                .add(maxLengthField()),
                                                           is(objectTypeLiteral())
                                                                                  .add(propertiesField())
                                                                                  .add(minPropertiesField())
                                                                                  .add(maxPropertiesField())
                                                                                  .add(additionalPropertiesField())
                                                                                  .add(discriminatorField())
                                                                                  .add(discriminatorValueField()),
                                                           // If it is an inherited type then we don't know we suggest all the properties
                                                           is(not(builtinTypes()))
                                                                                  .add(patternField())
                                                                                  .add(minLengthField())
                                                                                  .add(maxLengthField())
                                                                                  .add(enumField())
                                                                                  .add(formatField())
                                                                                  .add(uniqueItemsField())
                                                                                  .add(itemsField())
                                                                                  .add(minItemsField())
                                                                                  .add(maxItemsField())
                                                                                  .add(minimumField())
                                                                                  .add(maximumField())
                                                                                  .add(numberFormat())
                                                                                  .add(multipleOfField())
                                                                                  .add(fileTypesField())
                                                                                  .add(propertiesField())
                                                                                  .add(minPropertiesField())
                                                                                  .add(maxPropertiesField())
                                                                                  .add(additionalPropertiesField())
                                                                                  .add(discriminatorField())
                                                                                  .add(discriminatorValueField())
                                                                                  .add(field(facetRegex(), any()))
                                                   ).defaultValue(new TypeDefaultValue(defaultType))
                                           ).withAll(additionalRules)
                                           .then(TypeDeclarationNode.class);


                    }
                });
    }

    protected Rule builtinTypes()
    {
        final TypeId[] values = TypeId.values();
        final List<Rule> typeNames = new ArrayList<>();
        for (TypeId value : values)
        {
            typeNames.add(string(value.getType()));
        }
        return anyOf(typeNames);
    }

    protected KeyValueRule discriminatorValueField()
    {
        return field(string("discriminatorValue"), scalarType())
                                                                .description(
                                                                        "Identifies the declaring type."
                                                                                +
                                                                                " Requires including a discriminator facet in the type declaration."
                                                                                +
                                                                                " A valid value is an actual value that might identify the type of an individual object and is unique in the hierarchy of the type."
                                                                                +
                                                                                " Inline type declarations are not supported.");
    }

    protected KeyValueRule discriminatorField()
    {
        return field(string("discriminator"), scalarType())
                                                           .description(
                                                                   "Determines the concrete type of an individual object at runtime when, for example, payloads contain ambiguous types due to unions or inheritance."
                                                                           +
                                                                           " The value must match the name of one of the declared properties of a type. " +
                                                                           "Unsupported practices are inline type declarations and using discriminator with non-scalar properties.");
    }

    protected KeyValueRule additionalPropertiesField()
    {
        return field(string("additionalProperties"), booleanType())
                                                                   .description("A Boolean that indicates if an object instance has additional properties.");
    }

    protected KeyValueRule maxPropertiesField()
    {
        return field(string("maxProperties"), integerType())
                                                            .description("The maximum number of properties allowed for instances of this type.");
    }

    protected KeyValueRule minPropertiesField()
    {
        return field(string("minProperties"), integerType())
                                                            .description("The minimum number of properties allowed for instances of this type.");
    }

    protected KeyValueRule propertiesField()
    {
        return field(string("properties"), properties())
                                                        .description("The properties that instances of this type can or must have.");
    }

    protected KeyValueRule fileTypesField()
    {
        return field(string("fileTypes"), any())
                                                .description("A list of valid content-type strings for the file. The file type */* MUST be a valid value.");
    }

    protected KeyValueRule multipleOfField()
    {
        return field(string("multipleOf"), integerType())
                                                         .description(
                                                                 "A numeric instance is valid against \"multipleOf\" if the result of dividing the instance by this keyword's value is an integer.");
    }

    protected KeyValueRule numberFormat()
    {
        return field(string("format"), anyOf(string("int32"), string("int64"), string("int"), string("long"), string("float"), string("double"), string("int16"), string("int8")))
                                                                                                                                                                                  .description(
                                                                                                                                                                                          "The format of the value. The value MUST be one of the following: int32, int64, int, long, float, double, int16, int8");
    }

    protected KeyValueRule maximumField()
    {
        return field(string("maximum"), integerType())
                                                      .description("The maximum value of the parameter. Applicable only to parameters of type number or integer.");
    }

    protected KeyValueRule minimumField()
    {
        return field(string("minimum"), integerType())
                                                      .description("The minimum value of the parameter. Applicable only to parameters of type number or integer.");
    }

    protected KeyValueRule maxItemsField()
    {
        return field(string("maxItems"), integerType())
                                                       .description("Maximum amount of items in array. Value MUST be equal to or greater than 0.");
    }

    protected KeyValueRule minItemsField()
    {
        return field(string("minItems"), integerType())
                                                       .description("Minimum amount of items in array. Value MUST be equal to or greater than 0.");
    }

    protected KeyValueRule itemsField()
    {
        return field(string("items"), typeRef())
                                                .description("Indicates the type all items in the array are inherited from. Can be a reference to an existing type or an inline type declaration.");
    }

    protected KeyValueRule uniqueItemsField()
    {
        return field(string("uniqueItems"), booleanType())
                                                          .description("Boolean value that indicates if items in the array MUST be unique.");
    }

    protected KeyValueRule formatField()
    {
        return field(string("format"), anyOf(string("rfc3339"), string("rfc2616")));
    }

    protected KeyValueRule enumField()
    {
        return field(string("enum"), array(scalarType()))
                                                         .description(
                                                                 "Enumeration of possible values for this built-in scalar type. The value is an array containing representations of possible values, or a single value if there is only one possible value.");
    }

    protected KeyValueRule maxLengthField()
    {
        return field(string("maxLength"), integerType())
                                                        .description("Maximum length of the string. Value MUST be equal to or greater than 0.");
    }

    protected KeyValueRule minLengthField()
    {
        return field(string("minLength"), integerType())
                                                        .description("Minimum length of the string. Value MUST be equal to or greater than 0.");
    }

    protected KeyValueRule patternField()
    {
        return field(string("pattern"), scalarType())
                                                     .description("Regular expression that this string should match.");
    }

    protected KeyValueRule examplesField()
    {
        return field(exclusiveWith("examples", "example"), examplesValue())
                                                                           .description(
                                                                                   "Examples of instances of this type."
                                                                                           +
                                                                                           " This can be used, for example, by documentation generators to generate sample values for an object of this type."
                                                                                           +
                                                                                           " The \"examples\" facet MUST not be available when the \"example\" facet is already defined." +
                                                                                           " See section Examples for more information.");
    }

    protected KeyValueRule exampleField()
    {
        return field(exclusiveWith("example", "examples"), exampleValue())
                                                                          .then(ExampleDeclarationNode.class)
                                                                          .description(
                                                                                  "An example of an instance of this type that can be used, for example, by documentation generators to generate sample values for an object of this type."
                                                                                          +
                                                                                          " The \"example\" facet MUST not be available when the \"examples\" facet is already defined." +
                                                                                          " See section Examples for more information.");
    }

    protected KeyValueRule facetsField()
    {
        return field(string("facets"), objectType().with(field(facetRegex(), typeRef())))
                                                                                         .description(
                                                                                                 "A map of additional, user-defined restrictions that will be inherited and applied by any extending subtype. See section User-defined Facets for more information.");
    }

    private RegexValueRule facetRegex()
    {
        return regex("[^\\(].*");
    }

    protected ObjectRule examplesValue()
    {
        return objectType()
                           .with(field(scalarType(), exampleValue()).then(ExampleDeclarationNode.class));
    }

    private KeyValueRule typeField(TypeId defaultType)
    {
        return field(
                anyOf(typeKey(), schemaKey()),
                anyOf(typeExpressionReference(), array(typeExpressionReference()))).defaultValue(new TypeDefaultValue(defaultType))
                                                                                   .description(
                                                                                           "A base type which the current type extends or just wraps."
                                                                                                   +
                                                                                                   " The value of a type node MUST be either a) the name of a user-defined type or b) the name of a built-in RAML data type (object, array, or one of the scalar types) or c) an inline type declaration.");
    }

    private KeyValueRule requiredField()
    {
        return field(string("required"), booleanType());
    }

    private StringValueRule schemaKey()
    {
        return string(TYPES_FACET_SCHEMA);
    }

    private KeyValueRule xmlFacetField()
    {
        return field(string("xml"),
                objectType()
                            .with(attributeField())
                            .with(wrappedField())
                            .with(xmlNameField())
                            .with(namespaceField())
                            .with(prefixField()));
    }

    private KeyValueRule xmlNameField()
    {
        return field(string("name"), stringType()).description("Overrides the name of the XML element or XML attribute.");
    }

    private KeyValueRule prefixField()
    {
        return field(string("prefix"), stringType()).description("Configures the prefix used during serialization to XML.");
    }

    private KeyValueRule namespaceField()
    {
        return field(string("namespace"), stringType()).description("Configures the name of the XML namespace.");
    }

    private KeyValueRule wrappedField()
    {
        return field(string("wrapped"), booleanType()).description("true wraps a type instance in its own XML element. Cannot be true for scalar types or true at the same moment attribute is true.");
    }

    private KeyValueRule attributeField()
    {
        return field(string("attribute"), booleanType()).description("Serializes a type instance as an XML attribute. Can be true only for scalar types.");
    }

    private KeyValueRule defaultField()
    {
        return field(string("default"), any());
    }


    private AnyOfRule typeExpressionReference()
    {
        return anyOf(nullValue().then(NativeTypeExpressionNode.class),
                new SchemaDeclarationRule().then(ExternalSchemaTypeExpressionNode.class),
                new TypeExpressionReferenceRule().then(new TypeExpressionReferenceFactory()));
    }

    @Override
    protected Rule mimeType()
    {
        return anyOf(nullValue().then(new DefaultMimeTypeDeclarationFactory()), baseType(TypeId.ANY, "mimeType"));
    }

    protected Rule exampleValue()
    {
        return anyOf(explicitExample(), any());
    }

    private ObjectRule explicitExample()
    {
        return objectType()
                           .with(
                                   when("value",
                                           is(not(nullValue()))
                                                               .add(displayNameField())
                                                               .add(descriptionField())
                                                               .add(annotationField())
                                                               .add(field(string("value"), any()))
                                                               .add(field(string("strict"), booleanType())),
                                           is(nullValue())
                                                          .add(field(scalarType(), any()))
                                   ).defaultValue(new NullNodeImpl())
                           );
    }

    protected StringValueRule fileTypeLiteral()
    {
        return string("file");
    }

    protected Rule numericTypeLiteral()
    {
        return anyOf(numberTypeLiteral(), integerTypeLiteral());
    }

    protected Rule numberTypeLiteral()
    {
        return string("number");
    }

    protected Rule integerTypeLiteral()
    {
        return string("integer");
    }

    protected StringValueRule stringTypeLiteral()
    {
        return string("string");
    }

    private StringValueRule dateTimeTypeLiteral()
    {
        return string("datetime");
    }

    protected AnyOfRule arrayTypeLiteral()
    {
        return new AnyOfRule(regex(".+\\[\\]"), string("array"));
    }

    protected ObjectRule properties()
    {
        return objectType()
                           .with(propertyField());
    }

    private KeyValueRule propertyField()
    {
        return field(scalarType(), anyOf(inlineType(), propertyType())).then(PropertyNode.class);
    }

    private ObjectRule propertyType()
    {
        return baseType(TypeId.STRING, PROPERTY_TYPE_RULE, requiredField());
    }

    protected Rule objectTypeLiteral()
    {
        return string("object");
    }

    protected KeyValueRule mediaTypeField()
    {
        return field(mediaTypeKey(), anyOf(scalarType(), array(scalarType())));
    }

    protected Rule schemasValue()
    {
        return types();
    }

    @Nonnull
    protected String schemasDescription()
    {
        return "Alias for the equivalent \"types\" property, for compatibility " +
               "with RAML 0.8. Deprecated - API definitions should use the \"types\" property, " +
               "as the \"schemas\" alias for that property name may be removed in a future RAML version. " +
               "The \"types\" property allows for XML and JSON schemas.";
    }

    protected KeyValueRule displayNameField()
    {
        return field(displayNameKey(), ramlScalarValue()).defaultValue(new DisplayNameDefaultValue());
    }


    protected Rule resourceTypesValue()
    {
        return resourceTypes();
    }

    protected Rule securitySchemesValue()
    {
        return securitySchemes();
    }

    @Override
    protected Rule descriptionValue()
    {
        return firstOf(scalarType().then(new OverlayableSimpleTypeFactory(true)),
                annotatedScalarType(scalarType().then(new OverlayableSimpleTypeFactory(false))));
    }

    protected KeyValueRule docTitleField()
    {
        return requiredField(titleKey(),
                firstOf(
                        allOf(minLength(1), ramlScalarValue()),
                        annotatedScalarType(allOf(minLength(1), scalarType()))
                ));
    }

    @Override
    protected Rule titleValue()
    {
        return firstOf(
                allOf(minLength(1), scalarType().then(new OverlayableSimpleTypeFactory(true))),
                annotatedScalarType(allOf(minLength(1), scalarType().then(new OverlayableSimpleTypeFactory(false)))));
    }

    @Override
    public Rule ramlScalarValue()
    {
        return firstOf(scalarType().then(new RamlScalarValueFactory()), annotatedScalarType());
    }

    protected Rule annotatedScalarType()
    {
        return annotatedScalarType(scalarType());
    }

    protected Rule annotatedScalarType(Rule customScalarRule)
    {
        return objectType()
                           .with(field(string("value"), customScalarRule))
                           .with(annotationField());
    }

}
