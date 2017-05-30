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
package org.raml.v2.internal.impl.v08.grammar;

import org.raml.v2.internal.impl.commons.grammar.BaseRamlGrammar;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.rule.SchemaDeclarationRule;
import org.raml.v2.internal.impl.v08.nodes.DefaultParameterTypeValueNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.factory.InlineTypeDeclarationFactory;
import org.raml.v2.internal.impl.v10.nodes.factory.TypeExpressionReferenceFactory;
import org.raml.v2.internal.impl.v10.rules.TypeDefaultValue;
import org.raml.v2.internal.impl.v10.rules.TypeExpressionReferenceRule;
import org.raml.v2.internal.impl.v10.type.TypeId;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.yagi.framework.grammar.rule.ObjectRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nonnull;

public class Raml08Grammar extends BaseRamlGrammar
{

    @Override
    protected ObjectRule mimeType()
    {
        return objectType()
                           .with(field(string("schema"), schemaOrReference()))
                           .with(field(string("formParameters"), formParameters()))
                           .with(field(string("example"), scalarType()))
                           .then(TypeDeclarationNode.class);
    }

    protected Rule formParameters()
    {
        return objectType().with(field(scalarType(), anyOf(parameter(), array(parameter()))));
    }

    private AnyOfRule schemaOrReference()
    {
        return anyOf(explicitSchema(), new TypeExpressionReferenceRule().then(new TypeExpressionReferenceFactory()));
    }

    private Rule explicitSchema()
    {
        return new SchemaDeclarationRule().then(ExternalSchemaTypeExpressionNode.class);
    }

    protected Rule schemas()
    {
        return objectType()
                           .with(field(scalarType(), explicitSchema()))
                           .then(TypeDeclarationNode.class);
    }

    @Override
    protected ObjectRule resourceValue()
    {
        return super.resourceValue()
                    .with(field(string("baseUriParameters"), parameters()));

    }

    @Override
    protected ObjectRule methodValue()
    {
        return objectType()
                           .with(descriptionField())
                           .with(queryParametersField())
                           .with(headersField())
                           .with(responsesField())
                           .with(bodyField())
                           .with(protocolsField().description("A method can override the protocols specified in the resource or at the API root, by employing this property."))
                           .with(isField().description("A list of the traits to apply to this method."))
                           .with(securedByField().description("The security schemes that apply to this method."))
                           .with(field(string("baseUriParameters"), parameters()));
    }

    protected AnyOfRule anyMethod()
    {
        return anyOf(super.anyMethod(), string("connect"));
    }

    protected AnyOfRule anyOptionalMethod()
    {
        return anyOf(super.anyOptionalMethod(), string("connect?"));
    }


    @Override
    protected Rule parameter()
    {
        return anyOf(
                // Needs to be wrapped with anyOf in order to allow two then clauses
                anyOf(nullValue().then(NativeTypeExpressionNode.class)).then(new InlineTypeDeclarationFactory()),
                objectType()
                            .with(typeField())
                            .with(displayNameField())
                            .with(descriptionField())
                            .with(exampleField())
                            .with(defaultField())
                            .with(repeatField())
                            .with(requiredField())
                            .with(
                                    when("type",
                                            is(stringTypeLiteral())
                                                                   .add(patternField())
                                                                   .add(minLengthField())
                                                                   .add(maxLengthField())
                                                                   .add(enumField(scalarType())),
                                            is(numericTypeLiteral())
                                                                    .add(minimumField())
                                                                    .add(maximumField())
                                                                    .add(enumField(integerType())),
                                            is(fileTypeLiteral())
                                                                 .add(minLengthField())
                                                                 .add(maxLengthField())
                                    ).defaultValue(new DefaultParameterTypeValueNode())
                            ).then(TypeDeclarationNode.class));
    }

    private KeyValueRule defaultField()
    {
        return field(string("default"), any())
                                              .description("The default attribute specifies the default value to use for the property if the property is omitted or its value is not specified. " +
                                                           "This SHOULD NOT be interpreted as a requirement for the client to send the default attribute's value if there is no other value to send. " +
                                                           "Instead, the default attribute's value is the value the server uses if the client does not send a value.");
    }

    private KeyValueRule maximumField()
    {
        return field(string("maximum"), numberType()).description("The maximum attribute specifies the parameter's maximum value.");
    }

    private KeyValueRule minimumField()
    {
        return field(string("minimum"), numberType()).description("The minimum attribute specifies the parameter's minimum value.");
    }

    private KeyValueRule exampleField()
    {
        return field(string("example"), any()).description("The example attribute shows an example value for the property." +
                                                           " This can be used, e.g., by documentation generators to generate sample values for the property.");
    }

    private KeyValueRule repeatField()
    {
        return field(string("repeat"), booleanType()).description("The repeat attribute specifies that the parameter can be repeated. " +
                                                                  "If the parameter can be used multiple times, the repeat parameter value MUST be set to 'true'. " +
                                                                  "Otherwise, the default value is 'false' and the parameter may not be repeated.");
    }

    private KeyValueRule requiredField()
    {
        return field(string("required"), booleanType())
                                                       .description("The required attribute specifies whether the parameter and its value MUST be present in the API definition. " +
                                                                    "It must be either 'true' if the value MUST be present or 'false' otherwise.\n" +
                                                                    "\n" +
                                                                    "In general, parameters are optional unless the required attribute is included and its value set to 'true'.\n" +
                                                                    "\n" +
                                                                    "For a URI parameter, the required attribute MAY be omitted, but its default value is 'true'.");
    }

    private KeyValueRule maxLengthField()
    {
        return field(string("maxLength"), positiveIntegerType(true, (long) Integer.MAX_VALUE)).description("The maxLength attribute specifies the parameter value's maximum number of characters.");
    }

    private KeyValueRule minLengthField()
    {
        return field(string("minLength"), positiveIntegerType(true, (long) Integer.MAX_VALUE))
                                                                                              .description("The minLength attribute specifies the parameter value's minimum number of characters.");
    }

    private KeyValueRule enumField(Rule of)
    {
        return field(string("enum"), array(of))
                                               .description("The enum attribute provides an enumeration of the parameter's valid values. " +
                                                            "This MUST be an array. " +
                                                            "If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. " +
                                                            "If there is no matching value, the clients and servers MUST treat this as an error.");
    }

    private KeyValueRule patternField()
    {
        return field(string("pattern"), scalarType())
                                                     .description("The pattern attribute is a regular expression that a parameter of type string MUST match. " +
                                                                  "Regular expressions MUST follow the regular expression specification from ECMA 262/Perl 5. " +
                                                                  "The pattern MAY be enclosed in double quotes for readability and clarity.");
    }

    private KeyValueRule typeField()
    {
        return field(typeKey(), typeOptions()).defaultValue(new TypeDefaultValue(TypeId.STRING));
    }

    private Rule typeOptions()
    {
        AnyOfRule anyOfRule = anyOf(nullValue(), stringTypeLiteral(), numericTypeLiteral(), string("date"), fileTypeLiteral(), string("boolean"));
        applyThen(anyOfRule, NativeTypeExpressionNode.class);
        return anyOfRule;
    }

    private void applyThen(AnyOfRule anyOf, Class<? extends Node> nodeClass)
    {
        for (Rule rule : anyOf.getRules())
        {
            if (rule instanceof AnyOfRule)
            {
                applyThen((AnyOfRule) rule, nodeClass);
            }
            else
            {
                try
                {
                    rule.then(nodeClass);
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private StringValueRule fileTypeLiteral()
    {
        return string("file");
    }

    private Rule numericTypeLiteral()
    {
        return anyOf(string("number"), string("integer"));
    }

    private StringValueRule stringTypeLiteral()
    {
        return string("string");
    }

    protected Rule schemasValue()
    {
        return anyOf(array(schemas()), schemas());
    }

    @Nonnull
    protected String schemasDescription()
    {
        return "Collections of schemas that could be used anywhere in the API definition. " +
               "The \"schemas\" property allows for XML and JSON schemas.";
    }

    protected Rule traitsValue()
    {
        return anyOf(array(super.traitsValue()), super.traitsValue());
    }

    protected Rule resourceTypesValue()
    {
        return anyOf(array(resourceTypes()), resourceTypes());
    }

    protected Rule securitySchemesValue()
    {
        return anyOf(array(securitySchemes()), securitySchemes());
    }

    @Override
    protected ObjectRule securitySchemeSettings()
    {
        return super.securitySchemeSettings()
                    .with(field(string("authorizationUri"), scalarType()));
    }

}
