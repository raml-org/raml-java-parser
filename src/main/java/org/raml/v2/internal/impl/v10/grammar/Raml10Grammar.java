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

import org.raml.v2.internal.framework.grammar.rule.AnyOfRule;
import org.raml.v2.internal.framework.grammar.rule.ArrayWrapperFactory;
import org.raml.v2.internal.framework.grammar.rule.KeyValueRule;
import org.raml.v2.internal.framework.grammar.rule.NodeReferenceFactory;
import org.raml.v2.internal.framework.grammar.rule.ObjectRule;
import org.raml.v2.internal.framework.grammar.rule.RegexValueRule;
import org.raml.v2.internal.framework.grammar.rule.ResourceRefRule;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.grammar.rule.StringValueRule;
import org.raml.v2.internal.framework.nodes.NullNodeImpl;
import org.raml.v2.internal.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.internal.impl.commons.nodes.AnnotationNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.AnnotationTypeNode;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.ExtendsNode;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.rule.SchemaDeclarationRule;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.nodes.factory.InlineTypeDeclarationFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.TypeExpressionReferenceFactory;
import org.raml.v2.internal.impl.v10.rules.TypeDefaultValue;
import org.raml.v2.internal.impl.v10.rules.TypeExpressionReferenceRule;

import javax.annotation.Nonnull;

import static java.util.Arrays.asList;

public class Raml10Grammar extends BaseRamlGrammar
{

    public static final String ANNOTATION_TYPES_KEY_NAME = "annotationTypes";

    public ObjectRule raml()
    {
        return super.raml()
                    .with(annotationTypesField())
                    .with(annotationField())
                    .with(typesField())
                    .with(usesField());
    }


    @Override
    protected ObjectRule resourceValue()
    {
        return super.resourceValue().with(annotationField());
    }

    @Override
    protected ObjectRule methodValue()
    {
        return super.methodValue()
                    .with(field(queryStringKey(), anyOf(scalarType(), type())))
                    .with(annotationField());
    }

    @Override
    protected ObjectRule securitySchemePart()
    {
        return super.securitySchemePart().with(annotationField());
    }

    @Override
    protected ObjectRule securitySchemeSettings()
    {
        return super.securitySchemeSettings()
                    .with(field(string("signatures"), array(scalarType())))
                    .with(field(string("authorizationUri"), scalarType()).requiredWhen(new AuthorizationUriRequiredField()));
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
        return string("types")
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
        return field(titleKey(), scalarType());
    }


    public Rule libraryRef()
    {
        return new ResourceRefRule().then(LibraryLinkNode.class);
    }

    public ObjectRule libraryValue()
    {
        return objectType("library")
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
                           .with(field(scalarType(), type()));
    }

    protected Rule parameter()
    {
        return type();
    }

    public Rule type()
    {
        return anyOf(inlineType(), explicitType());
    }

    private AnyOfRule typeRef()
    {
        return anyOf(inlineType(), ref("explicitType"));
    }

    protected Rule inlineType()
    {
        return typeExpressionReference().then(new InlineTypeDeclarationFactory());
    }

    public ObjectRule explicitType()
    {
        return objectType("explicitType")
                                         .with(typeField())
                                         .with(xmlFacetField())
                                         .with(displayNameField())
                                         .with(descriptionField())
                                         .with(usageField())
                                         .with(annotationField())
                                         .with(defaultField())
                                         .with(requiredField())
                                         .with(field(exclusiveWith("example", "examples"), exampleValue()).then(ExampleDeclarationNode.class))
                                         .with(field(exclusiveWith("examples", "example"), examplesValue()))
                                         .with(
                                                 when(asList("type", "schema"),
                                                         is(stringTypeLiteral())
                                                                                .add(field(string("pattern"), scalarType()))
                                                                                .add(field(string("minLength"), integerType()))
                                                                                .add(field(string("maxLength"), integerType()))
                                                                                .add(field(string("enum"), array(scalarType()))),
                                                         is(dateTimeTypeLiteral())
                                                                                  .add(field(string("format"), anyOf(string("rfc3339"), string("rfc2616")))),
                                                         is(arrayTypeLiteral())
                                                                               .add(field(string("uniqueItems"), booleanType()))
                                                                               .add(field(string("items"), typeRef()))
                                                                               .add(field(string("minItems"), integerType()))
                                                                               .add(field(string("maxItems"), integerType())),
                                                         is(numericTypeLiteral())
                                                                                 .add(field(string("minimum"), integerType()))
                                                                                 .add(field(string("maximum"), integerType()))
                                                                                 .add(field(string("format"), scalarType()))
                                                                                 .add(field(string("multipleOf"), integerType()))
                                                                                 .add(field(string("enum"), array(integerType()))),
                                                         is(fileTypeLiteral())
                                                                              .add(field(string("fileTypes"), any()))
                                                                              .add(field(string("minLength"), integerType()))
                                                                              .add(field(string("maxLength"), integerType())),
                                                         is(objectTypeLiteral())
                                                                                .add(field(string("properties"), properties()))
                                                                                .add(field(string("minProperties"), integerType()))
                                                                                .add(field(string("maxProperties"), integerType()))
                                                                                .add(field(string("additionalProperties"), booleanType()))
                                                                                .add(field(string("patternProperties"), properties()))
                                                                                .add(field(string("discriminator"), scalarType()))
                                                                                .add(field(string("discriminatorValue"), scalarType())),
                                                         // If it is an inherited type then we don't know we suggest all the properties
                                                         is(any())
                                                                  .add(field(string("pattern"), scalarType()))
                                                                  .add(field(string("minLength"), integerType()))
                                                                  .add(field(string("maxLength"), integerType()))
                                                                  .add(field(string("enum"), array(scalarType())))
                                                                  .add(field(string("format"), anyOf(string("rfc3339"), string("rfc2616"))))
                                                                  .add(field(string("uniqueItems"), booleanType()))
                                                                  .add(field(string("items"), typeRef()))
                                                                  .add(field(string("minItems"), integerType()))
                                                                  .add(field(string("maxItems"), integerType()))
                                                                  .add(field(string("fileTypes"), any()))
                                                                  .add(field(string("minLength"), integerType()))
                                                                  .add(field(string("maxLength"), integerType()))
                                                                  .add(field(string("fileTypes"), any()))
                                                                  .add(field(string("minLength"), integerType()))
                                                                  .add(field(string("maxLength"), integerType()))
                                                                  .add(field(string("properties"), properties()))
                                                                  .add(field(string("minProperties"), integerType()))
                                                                  .add(field(string("maxProperties"), integerType()))
                                                                  .add(field(string("additionalProperties"), booleanType()))
                                                                  .add(field(string("patternProperties"), properties()))
                                                                  .add(field(string("discriminator"), scalarType()))
                                                                  .add(field(string("discriminatorValue"), scalarType()))
                                                 ).defaultValue(new TypeDefaultValue())
                                         ).then(TypeDeclarationNode.class);
    }

    protected ObjectRule examplesValue()
    {
        return objectType()
                           .with(field(scalarType(), exampleValue()).then(ExampleDeclarationNode.class));
    }

    private KeyValueRule typeField()
    {
        return field(
                anyOf(typeKey(), schemaKey()),
                anyOf(typeExpressionReference(), array(typeExpressionReference()))).defaultValue(new TypeDefaultValue());
    }

    private KeyValueRule requiredField()
    {
        return field(string("required"), booleanType());
    }

    private StringValueRule schemaKey()
    {
        return string("schema");
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
        return explicitType();
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
        return field(scalarType(), typeRef()).then(PropertyNode.class);
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

    protected Rule traitsValue()
    {
        return trait();
    }

    protected Rule resourceTypesValue()
    {
        return resourceTypes();
    }

    protected Rule securitySchemesValue()
    {
        return securitySchemes();
    }

}
