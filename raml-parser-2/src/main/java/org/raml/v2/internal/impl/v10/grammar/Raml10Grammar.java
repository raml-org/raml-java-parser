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

import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.v2.internal.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.internal.impl.commons.nodes.AnnotationNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationTypeNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationTypesNode;
import org.raml.v2.internal.impl.commons.nodes.CustomFacetDefinitionNode;
import org.raml.v2.internal.impl.commons.nodes.DocumentationItemNode;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.ExamplesNode;
import org.raml.v2.internal.impl.commons.nodes.ExtendsNode;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.FacetNode;
import org.raml.v2.internal.impl.commons.nodes.ResponseNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypesNode;
import org.raml.v2.internal.impl.commons.rule.NodeReferenceFactory;
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
import org.raml.yagi.framework.grammar.RuleFactory;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.grammar.rule.ArrayWrapperFactory;
import org.raml.yagi.framework.grammar.rule.ConditionalRule;
import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.yagi.framework.grammar.rule.ObjectRule;
import org.raml.yagi.framework.grammar.rule.RegexValueRule;
import org.raml.yagi.framework.grammar.rule.ResourceRefRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;
import org.raml.yagi.framework.nodes.NullNodeImpl;


public class Raml10Grammar extends BaseRamlGrammar
{

    public static final String ANNOTATION_TYPES_KEY_NAME = "annotationTypes";
    public static final String DEFAULT_TYPE_RULE = "defaultTypeRule";
    public static final String PROPERTY_TYPE_RULE = "propertyTypeRule";
    public static final String TYPES_FACET_TYPE = "type";
    public static final String TYPES_FACET_SCHEMA = "schema";
    public static final String MIN_ITEMS_KEY_NAME = "minItems";
    public static final String MAX_ITEMS_KEY_NAME = "maxItems";
    public static final String UNIQUE_ITEMS_KEY_NAME = "uniqueItems";
    public static final String ITEMS_KEY_NAME = "items";
    public static final String FILE_TYPES_KEY_NAME = "fileTypes";
    public static final String MIN_LENGTH_KEY_NAME = "minLength";
    public static final String MAX_LENGTH_KEY_NAME = "maxLength";
    public static final String MINIMUM_KEY_NAME = "minimum";
    public static final String MAXIMUM_KEY_NAME = "maximum";
    public static final String FORMAT_KEY_NAME = "format";
    public static final String ENUM_KEY_NAME = "enum";
    public static final String MULTIPLE_OF_KEY_NAME = "multipleOf";
    public static final String PROPERTIES_KEY_NAME = "properties";
    public static final String MIN_PROPERTIES_KEY_NAME = "minProperties";
    public static final String MAX_PROPERTIES_KEY_NAME = "maxProperties";
    public static final String ADDITIONAL_PROPERTIES_KEY_NAME = "additionalProperties";
    public static final String DISCRIMINATOR_KEY_NAME = "discriminator";
    public static final String DISCRIMINATOR_VALUE_KEY_NAME = "discriminatorValue";
    public static final String PATTERN_KEY_NAME = "pattern";

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
                                          .with(methodField())
                                          .with(resourceField())
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
    public ObjectRule securityScheme()
    {
        return super.securityScheme().with(annotationField());
    }

    @Override
    protected ObjectRule securitySchemePart()
    {
        return super.securitySchemePart()
                    .with(annotationField())
                    .with(field(queryStringKey(), type()))
                    .with(exclusiveKeys(QUERY_STRING_KEY_NAME, QUERY_PARAMETERS_KEY_NAME));
    }

    @Override
    protected ObjectRule securitySchemeSettings()
    {
        return objectType()
                           .with(annotationField())
                           .with(
                                   when("../../type",
                                           is(string(OAUTH_1_0))
                                                                .add(requiredField(string("requestTokenUri"), ramlScalarValue()))
                                                                .add(requiredField(string("authorizationUri"), ramlScalarValue()))
                                                                .add(requiredField(string("tokenCredentialsUri"), ramlScalarValue()))
                                                                .add(field(string("signatures"), array(scalarType()))),
                                           is(string(OAUTH_2_0))
                                                                .add(field(string("authorizationUri"), ramlScalarValue()).requiredWhen(new AuthorizationUriRequiredField()))
                                                                .add(requiredField(string("accessTokenUri"), ramlScalarValue()))
                                                                .add(requiredField(string("authorizationGrants"), anyOf(authorizationGrantsValue(), array(authorizationGrantsValue()))))
                                                                .add(field(string("scopes"), anyOf(scalarType(), array(scalarType()))))
                                   ));
    }

    @Override
    protected Rule authorizationGrantsValue()
    {
        return anyOf(string("authorization_code"),
                string("password"),
                string("client_credentials"),
                string("implicit"),
                regex("urn:.*"),
                regex("http://.*"),
                regex("https://.*"));
    }

    @Override
    protected KeyValueRule responseField()
    {
        return super.responseField().then(ResponseNode.class);
    }

    @Override
    protected ObjectRule response()
    {
        return super.response().with(annotationField());
    }

    protected KeyValueRule typesField()
    {
        return field(typesKey(), types()).then(TypesNode.class);
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
        return field(annotationTypesKey(), annotationTypes()).then(AnnotationTypesNode.class);
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
        return field(string("allowedTargets"), anyOf(allowedTargetValues(), array(allowedTargetValues())).then(new ArrayWrapperFactory()));
    }

    private Rule allowedTargetValues()
    {
        List<Rule> values = new ArrayList<>();
        for (AnnotationTarget annotationTarget : AnnotationTarget.values())
        {
            values.add(string(annotationTarget.name()));
        }
        return anyOf(values);
    }

    protected Rule types()
    {
        return objectType()
                           .with(field(ramlTypeKey(), type()).then(TypeDeclarationField.class));
    }

    protected Rule ramlTypeKey()
    {
        List<Rule> types = new ArrayList<>();
        for (TypeId typeId : TypeId.values())
        {
            types.add(string(typeId.getType()));
        }
        return not(anyOf(types));
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
        return anyOf(typeExpressionReference(), array(typeExpressionReference()))
                                                                                 .then(new InlineTypeDeclarationFactory());
    }

    public ObjectRule explicitType()
    {
        return baseType(TypeId.STRING, DEFAULT_TYPE_RULE);
    }

    private ObjectRule baseType(final TypeId defaultType, final String ruleName)
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
                                           .with(requiredTypeField())
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
                                                           is(integerTypeLiteral())
                                                                                   .add(minimumField(integerType()))
                                                                                   .add(maximumField(integerType()))
                                                                                   .add(numberFormat())
                                                                                   .add(enumField())
                                                                                   .add(multipleOfField(integerType())),
                                                           is(numericTypeLiteral())
                                                                                   .add(minimumField(numberType()))
                                                                                   .add(maximumField(numberType()))
                                                                                   .add(numberFormat())
                                                                                   .add(enumField())
                                                                                   .add(multipleOfField(numberType())),
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
                                                                                  .add(patternField().matchValue())
                                                                                  .add(minLengthField().matchValue())
                                                                                  .add(maxLengthField().matchValue())
                                                                                  .add(enumField().matchValue())
                                                                                  .add(formatField().matchValue())
                                                                                  .add(numberFormat().matchValue())
                                                                                  .add(uniqueItemsField().matchValue())
                                                                                  .add(itemsField().matchValue())
                                                                                  .add(minItemsField().matchValue())
                                                                                  .add(maxItemsField().matchValue())
                                                                                  .add(minimumField(numberType()).matchValue())
                                                                                  .add(maximumField(numberType()).matchValue())
                                                                                  .add(multipleOfField(numberType()).matchValue())
                                                                                  .add(fileTypesField().matchValue())
                                                                                  .add(propertiesField().matchValue())
                                                                                  .add(minPropertiesField().matchValue())
                                                                                  .add(maxPropertiesField().matchValue())
                                                                                  .add(additionalPropertiesField().matchValue())
                                                                                  .add(discriminatorField().matchValue())
                                                                                  .add(discriminatorValueField().matchValue())
                                                                                  .add(customFacetField().matchValue())
                                                   ).defaultValue(new TypeDefaultValue(defaultType))
                                           ).then(TypeDeclarationNode.class);


                    }
                });
    }

    private KeyValueRule customFacetField()
    {
        return field(facetRegex(), any()).then(FacetNode.class);
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

    public KeyValueRule discriminatorValueField()
    {
        return field(string(DISCRIMINATOR_VALUE_KEY_NAME), scalarType())
                                                                        .then(FacetNode.class)
                                                                        .description(
                                                                                "Identifies the declaring type."
                                                                                        +
                                                                                        " Requires including a discriminator facet in the type declaration."
                                                                                        +
                                                                                        " A valid value is an actual value that might identify the type of an individual object and is unique in the hierarchy of the type."
                                                                                        +
                                                                                        " Inline type declarations are not supported.");
    }

    public KeyValueRule discriminatorField()
    {
        return field(string(DISCRIMINATOR_KEY_NAME), scalarType())
                                                                  .then(FacetNode.class)
                                                                  .description(
                                                                          "Determines the concrete type of an individual object at runtime when, for example, payloads contain ambiguous types due to unions or inheritance."
                                                                                  +
                                                                                  " The value must match the name of one of the declared properties of a type. " +
                                                                                  "Unsupported practices are inline type declarations and using discriminator with non-scalar properties.");
    }

    public KeyValueRule additionalPropertiesField()
    {
        return field(string(ADDITIONAL_PROPERTIES_KEY_NAME), booleanType())
                                                                           .then(FacetNode.class)
                                                                           .description("A Boolean that indicates if an object instance has additional properties.");
    }

    public KeyValueRule maxPropertiesField()
    {
        return field(string(MAX_PROPERTIES_KEY_NAME), integerType())
                                                                    .then(FacetNode.class)
                                                                    .description("The maximum number of properties allowed for instances of this type.");
    }

    public KeyValueRule minPropertiesField()
    {
        return field(string(MIN_PROPERTIES_KEY_NAME), integerType())
                                                                    .then(FacetNode.class)
                                                                    .description("The minimum number of properties allowed for instances of this type.");
    }

    public KeyValueRule propertiesField()
    {
        return field(string(PROPERTIES_KEY_NAME), properties())
                                                               .then(FacetNode.class)
                                                               .description("The properties that instances of this type can or must have.");
    }

    public KeyValueRule fileTypesField()
    {
        return field(string(FILE_TYPES_KEY_NAME), any())
                                                        .then(FacetNode.class)
                                                        .description("A list of valid content-type strings for the file. The file type */* MUST be a valid value.");
    }

    public KeyValueRule multipleOfField(Rule rule)
    {
        return field(string(MULTIPLE_OF_KEY_NAME), rule)
                                                        .then(FacetNode.class)
                                                        .description(
                                                                "A numeric instance is valid against \"multipleOf\" if the result of dividing the instance by this keyword's value is an integer.");
    }

    public KeyValueRule numberFormat()
    {
        return field(string(FORMAT_KEY_NAME), anyOf(string("int32"), string("int64"), string("int"), string("long"), string("float"), string("double"), string("int16"), string("int8")))
                                                                                                                                                                                         .then(FacetNode.class)
                                                                                                                                                                                         .description(
                                                                                                                                                                                                 "The format of the value. The value MUST be one of the following: int32, int64, int, long, float, double, int16, int8");
    }

    public KeyValueRule maximumField(Rule rule)
    {
        return field(string(MAXIMUM_KEY_NAME), rule)
                                                    .then(FacetNode.class)
                                                    .description("The maximum value of the parameter. Applicable only to parameters of type number or integer.");
    }

    public KeyValueRule minimumField(Rule rule)
    {
        return field(string(MINIMUM_KEY_NAME), rule)
                                                    .then(FacetNode.class)
                                                    .description("The minimum value of the parameter. Applicable only to parameters of type number or integer.");
    }

    public KeyValueRule maxItemsField()
    {
        return field(string(MAX_ITEMS_KEY_NAME), integerType())
                                                               .then(FacetNode.class)
                                                               .description("Maximum amount of items in array. Value MUST be equal to or greater than 0.");
    }

    public KeyValueRule minItemsField()
    {
        return field(string(MIN_ITEMS_KEY_NAME), integerType())
                                                               .then(FacetNode.class)
                                                               .description("Minimum amount of items in array. Value MUST be equal to or greater than 0.");
    }

    public KeyValueRule itemsField()
    {
        return field(string(ITEMS_KEY_NAME), typeRef())
                                                       .then(FacetNode.class)
                                                       .description(
                                                               "Indicates the type all items in the array are inherited from. Can be a reference to an existing type or an inline type declaration.");
    }

    public KeyValueRule uniqueItemsField()
    {
        return field(string(UNIQUE_ITEMS_KEY_NAME), booleanType())
                                                                  .then(FacetNode.class)
                                                                  .description("Boolean value that indicates if items in the array MUST be unique.");
    }

    public KeyValueRule formatField()
    {
        return field(string(FORMAT_KEY_NAME), anyOf(string("rfc3339"), string("rfc2616"))).then(FacetNode.class);
    }

    public KeyValueRule enumField()
    {
        return field(string(ENUM_KEY_NAME), array(scalarType()))
                                                                .then(FacetNode.class)
                                                                .description(
                                                                        "Enumeration of possible values for this built-in scalar type. The value is an array containing representations of possible values, or a single value if there is only one possible value.");
    }

    public KeyValueRule maxLengthField()
    {
        return field(string(MAX_LENGTH_KEY_NAME), integerType())
                                                                .then(FacetNode.class)
                                                                .description("Maximum length of the string. Value MUST be equal to or greater than 0.");
    }

    public KeyValueRule minLengthField()
    {
        return field(string(MIN_LENGTH_KEY_NAME), integerType()).then(FacetNode.class)
                                                                .description("Minimum length of the string. Value MUST be equal to or greater than 0.");
    }

    public KeyValueRule patternField()
    {
        return field(string(PATTERN_KEY_NAME), scalarType())
                                                            .then(FacetNode.class)
                                                            .description("Regular expression that this string should match.");
    }

    protected KeyValueRule examplesField()
    {
        return field(exclusiveWith("examples", "example"), examplesValue()).then(ExamplesNode.class)
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
        return field(string("facets"), objectType()
                                                   .with(field(facetRegex(), typeRef()).then(CustomFacetDefinitionNode.class)))
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
                anyOf(typeExpressionReference(), array(typeExpressionReference()), explicitType()))
                                                                                                   .defaultValue(new TypeDefaultValue(defaultType))
                                                                                                   .description(
                                                                                                           "A base type which the current type extends or just wraps."
                                                                                                                   +
                                                                                                                   " The value of a type node MUST be either a) the name of a user-defined type or b) the name of a built-in RAML data type (object, array, or one of the scalar types) or c) an inline type declaration.");
    }

    private KeyValueRule requiredTypeField()
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
        return anyOf(nullValue().then(new DefaultMimeTypeDeclarationFactory()), anyOf(inlineType(), baseType(TypeId.ANY, "mimeType")));
    }

    protected Rule exampleValue()
    {
        return anyOf(explicitExample(), any());
    }

    public Rule exampleFragment()
    {
        return anyOf(explicitExample(true), any());
    }

    private ObjectRule explicitExample()
    {
        return explicitExample(false);
    }

    private ObjectRule explicitExample(boolean isFragment)
    {
        ConditionalRule nestedValue = is(not(nullValue()))
                                                          .add(displayNameField())
                                                          .add(descriptionField())
                                                          .add(annotationField())
                                                          .add(field(string("value"), any()))
                                                          .add(field(string("strict"), booleanType()));
        if (isFragment)
        {
            nestedValue.add(usesField());
        }
        return objectType()
                           .with(
                                   when("value",
                                           nestedValue,
                                           is(nullValue()).add(field(scalarType(), any()))
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
                           .with(propertyField()).named("Properties");
    }

    @Override
    protected Rule parameters()
    {
        return properties();
    }

    private KeyValueRule propertyField()
    {
        return field(scalarType(), anyOf(inlineType(), propertyType())).then(PropertyNode.class);
    }

    private ObjectRule propertyType()
    {
        return baseType(TypeId.STRING, PROPERTY_TYPE_RULE).named("PropertyTypeRule");
    }

    protected Rule objectTypeLiteral()
    {
        return string("object");
    }

    protected KeyValueRule mediaTypeField()
    {
        return field(mediaTypeKey(), anyOf(mimeTypeRegex(), array(mimeTypeRegex())));
    }

    protected KeyValueRule schemasField()
    {
        return field(schemasKey(), schemasValue()).then(TypesNode.class);
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
        return field(displayNameKey(), overlayableRamlScalarValue()).defaultValue(new DisplayNameDefaultValue());
    }

    protected Rule overlayableRamlScalarValue()
    {
        return firstOf(scalarType().then(new OverlayableSimpleTypeFactory(true)),
                annotatedScalarType(scalarType().then(new OverlayableSimpleTypeFactory(false))));
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
        return overlayableRamlScalarValue();
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

    public ObjectRule documentation()
    {
        return super.documentation().with(annotationField()).then(DocumentationItemNode.class);
    }

}
