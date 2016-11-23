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
package org.raml.v2.api;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.ExternalTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.StringTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.Trait;
import org.raml.v2.api.model.v10.methods.TraitRef;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.resources.ResourceType;
import org.raml.v2.api.model.v10.resources.ResourceTypeRef;
import org.raml.v2.api.model.v10.security.SecurityScheme;
import org.raml.v2.api.model.v10.security.SecuritySchemePart;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;
import org.raml.v2.api.model.v10.security.SecuritySchemeSettings;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;

public class SpecInterfacesV10TestCase
{

    @Test
    public void full() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/full/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();

        assertApi(api);
    }

    private void assertAnnotationTypes(List<TypeDeclaration> annotationTypes)
    {
        assertThat(annotationTypes.size(), is(2));
        TypeDeclaration basic = annotationTypes.get(0);
        assertThat(basic.name(), is("basic"));
        assertThat(basic.type(), is("string"));
        TypeDeclaration hipermedia = annotationTypes.get(1);
        assertThat(hipermedia.name(), is("complex"));
        assertThat(hipermedia, is(instanceOf(ObjectTypeDeclaration.class)));

        ObjectTypeDeclaration object = (ObjectTypeDeclaration) hipermedia;

        TypeDeclaration property = object.properties().get(0);
        assertThat(property.name(), is("controls"));
        assertThat(property, is(instanceOf(ObjectTypeDeclaration.class)));

        ObjectTypeDeclaration object1 = (ObjectTypeDeclaration) property;

        TypeDeclaration property1 = object1.properties().get(2);
        assertThat(property1.name(), is("names"));
        assertThat(property1, is(instanceOf(ArrayTypeDeclaration.class)));

        ArrayTypeDeclaration array = (ArrayTypeDeclaration) property1;
        assertThat(array.items(), is(instanceOf(StringTypeDeclaration.class)));
    }

    private void assertApi(Api api)
    {
        assertThat(api.title().value(), is("api title"));
        assertThat(api.description().value(), is("api description"));
        assertScalarAnnotation(api.title(), "title");
        assertThat(api.version().value(), is("1.0"));
        assertThat(api.version().value(), instanceOf(String.class));
        assertThat(api.version().annotations(), hasSize(0));
        assertThat(api.baseUri().value(), is("http://base.uri/{version}/{param1}/{param2}/{param3}/{param4}"));
        assertBaseUriParameters(api.baseUriParameters());
        assertThat(api.protocols().size(), is(2));
        assertThat(api.protocols().get(0), is("HTTP"));
        assertThat(api.protocols().get(1), is("HTTPS"));
        assertThat(api.mediaType().size(), is(1));
        assertThat(api.mediaType().get(0).value(), is("application/json"));
        assertSecuredBy(api.securedBy());
        assertResources(api.resources());
        assertDocumentation(api.documentation());
        assertThat(api.ramlVersion(), is("1.0"));
        assertThat(api.schemas().size(), is(0));

        assertTypes(api.types());
        assertTraits(api.traits());
        assertResourceTypes(api.resourceTypes());
        assertAnnotationTypes(api.annotationTypes());
        assertSecuritySchemes(api.securitySchemes());
        assertAnnotations(api.annotations());
    }

    private void assertTypes(List<TypeDeclaration> types)
    {
        assertThat(types, hasSize(7));

        // object type
        ObjectTypeDeclaration user = (ObjectTypeDeclaration) types.get(0);
        assertThat(user.name(), is("User"));
        assertThat(user.additionalProperties(), is(true));
        assertThat(user.discriminator(), nullValue());
        assertThat(user.discriminatorValue(), nullValue());
        assertThat(user.maxProperties(), nullValue());
        List<TypeDeclaration> properties = user.properties();
        assertThat(properties, hasSize(5));
        assertUserProperties(properties);
        assertUserExamples(user.examples());
        List<TypeDeclaration> parentTypesUser = user.parentTypes();
        assertThat(parentTypesUser, hasSize(1));
        assertThat(parentTypesUser.get(0).name(), is("object"));
        assertThat(parentTypesUser.get(0).parentTypes(), hasSize(0));

        // inherited object type
        ObjectTypeDeclaration superUser = (ObjectTypeDeclaration) types.get(1);
        assertThat(superUser.name(), is("SuperUser"));
        assertThat(superUser.type(), is("User"));
        properties = superUser.properties();
        assertThat(properties, hasSize(6));
        assertUserProperties(properties);
        ArrayTypeDeclaration skills = (ArrayTypeDeclaration) properties.get(5);
        assertThat(skills.maxItems(), is(3));
        assertThat(skills.type(), is("string[]"));
        assertThat(superUser.examples(), hasSize(0));
        List<TypeDeclaration> parentTypesSuperUser = superUser.parentTypes();
        assertThat(parentTypesSuperUser, hasSize(1));
        assertThat(parentTypesSuperUser.get(0).name(), is("User"));
        List<TypeDeclaration> parentTypesSuperUserUser = parentTypesSuperUser.get(0).parentTypes();
        assertThat(parentTypesSuperUserUser, hasSize(1));
        assertThat(parentTypesSuperUserUser.get(0).name(), is("object"));
        assertThat(parentTypesSuperUserUser.get(0).parentTypes(), hasSize(0));

        // string type
        StringTypeDeclaration nString = (StringTypeDeclaration) types.get(3);
        assertThat(nString.maxLength(), is(10));
        assertThat(nString.pattern(), nullValue());
        List<TypeDeclaration> parentTypesString = nString.parentTypes();
        assertThat(parentTypesString, hasSize(1));
        assertThat(parentTypesString.get(0).name(), is("string"));
        assertThat(parentTypesString.get(0).parentTypes(), hasSize(0));

        // json schema type
        ExternalTypeDeclaration jsonSchema = (ExternalTypeDeclaration) types.get(4);
        List<TypeDeclaration> parentTypesJson = jsonSchema.parentTypes();
        assertThat(parentTypesJson, hasSize(1));
        assertThat(parentTypesJson.get(0).name(), nullValue());
        assertThat(parentTypesJson.get(0).parentTypes(), hasSize(0));

        // Type with custom facets
        TypeDeclaration myType = types.get(6);
        MatcherAssert.assertThat(myType.name(), is("TypeWithCustomFacets"));

        List<TypeDeclaration> facets = myType.facets();
        TypeDeclaration facet1 = facets.get(0);
        MatcherAssert.assertThat(facet1.name(), is("facet1"));
        MatcherAssert.assertThat(facet1.type(), is("integer"));
        MatcherAssert.assertThat(facet1.required(), is(false));

        TypeDeclaration facet2 = facets.get(1);
        MatcherAssert.assertThat(facet2.name(), is("facet2"));
        MatcherAssert.assertThat(facet2.type(), is("integer"));
        MatcherAssert.assertThat(facet2.required(), is(false));

        TypeDeclaration facet3 = facets.get(2);
        MatcherAssert.assertThat(facet3.name(), is("facet3?"));
        MatcherAssert.assertThat(facet3.type(), is("integer"));
        MatcherAssert.assertThat(facet3.required(), is(true));

        TypeDeclaration facet4 = facets.get(3);
        MatcherAssert.assertThat(facet4.name(), is("facet4?"));
        MatcherAssert.assertThat(facet4.type(), is("integer"));
        MatcherAssert.assertThat(facet4.required(), is(false));
    }

    private void assertUserProperties(List<TypeDeclaration> properties)
    {
        StringTypeDeclaration firstName = (StringTypeDeclaration) properties.get(0);
        assertThat(firstName.name(), is("firstname"));
        assertThat(firstName.enumValues(), hasSize(0));
        assertThat(properties.get(1).name(), is("lastname"));
        IntegerTypeDeclaration age = (IntegerTypeDeclaration) properties.get(2);
        assertThat(age.name(), is("age"));
        assertThat(age.minimum(), closeTo(0, 0.1));
        assertThat(age.maximum(), closeTo(144, 0.1));
        assertThat(age.enumValues(), hasSize(0));
    }

    private void assertUserExamples(List<ExampleSpec> examples)
    {
        assertThat(examples, hasSize(3));
        ExampleSpec batman = examples.get(0);
        assertThat(batman.name(), is("batman"));
        List<TypeInstanceProperty> batmanProps = batman.structuredValue().properties();
        assertThat(batmanProps, hasSize(4));
        assertThat(batmanProps.get(0).name(), is("firstname"));
        assertTrue(batmanProps.get(0).value().isScalar());
        assertThat(batmanProps.get(0).value().value().toString(), is("bruce"));
        assertThat(batmanProps.get(1).name(), is("lastname"));
        assertTrue(batmanProps.get(1).value().isScalar());
        assertThat(batmanProps.get(1).value().value().toString(), is("wayne"));
        assertThat(batmanProps.get(2).name(), is("age"));
        assertTrue(batmanProps.get(2).value().isScalar());
        assertThat(batmanProps.get(2).value().value().toString(), is("77"));
        assertThat(batmanProps.get(3).name(), is("height"));
        assertTrue(batmanProps.get(3).value().isScalar());
        assertThat(batmanProps.get(3).value().value().toString(), is("1.82"));

        assertThat(examples.get(1).name(), is("daredevil"));

    }

    private void assertAnnotations(List<AnnotationRef> annotations)
    {
        assertThat(annotations.size(), is(2));

        AnnotationRef basic = annotations.get(0);
        assertThat(basic.name(), is("(basic)"));
        assertThat(basic.annotation().name(), is("basic"));
        TypeInstance basicValue = basic.structuredValue();
        assertTrue(basicValue.isScalar());
        assertThat(basicValue.value().toString(), is("sometimes"));
        assertThat(basic.annotation().type(), is("string"));

        TypeInstance complexValue = annotations.get(1).structuredValue();
        assertThat(complexValue.properties().size(), is(2));
        assertThat(complexValue.properties().get(0).name(), is("controls"));
        assertThat(complexValue.properties().get(1).name(), is("permanentUri"));
        assertThat(complexValue.properties().get(1).value().isScalar(), is(true));
        assertThat(complexValue.properties().get(1).value().value().toString(), is("false"));
        List<TypeInstanceProperty> controlProps = complexValue.properties().get(0).value().properties();
        assertThat(controlProps.size(), is(3));
        assertThat(controlProps.get(0).name(), is("url"));
        assertThat(controlProps.get(0).value().isScalar(), is(true));
        assertThat(controlProps.get(0).value().value().toString(), is("here"));
        assertThat(controlProps.get(1).name(), is("property"));
        assertThat(controlProps.get(1).value().isScalar(), is(true));
        assertThat(controlProps.get(1).value().value().toString(), is("off"));
        assertThat(controlProps.get(2).name(), is("names"));
        assertThat(controlProps.get(2).isArray(), is(true));
        assertThat(controlProps.get(2).values().size(), is(2));
        assertThat(controlProps.get(2).values().get(0).isScalar(), is(true));
        assertThat(controlProps.get(2).values().get(0).value().toString(), is("one"));

    }

    private void assertSecuritySchemes(List<SecurityScheme> securitySchemes)
    {
        assertThat(securitySchemes.size(), is(2));
        assertOauth2SecurityScheme(securitySchemes.get(0));
    }

    private void assertSecuredBy(List<SecuritySchemeRef> securedBy)
    {
        assertThat(securedBy.size(), is(3));

        assertThat(securedBy.get(0).name(), is("oauth_2_0"));
        assertOauth2SecurityScheme(securedBy.get(0).securityScheme());

        assertThat(securedBy.get(1).name(), is("oauth_1_0"));
        assertOauth1SecurityScheme(securedBy.get(1).securityScheme());

        SecuritySchemeRef noSecurity = securedBy.get(2);
        assertNull(noSecurity);
    }

    private void assertOauth1SecurityScheme(SecurityScheme oauth1)
    {
        assertThat(oauth1.name(), is("oauth_1_0"));
        assertThat(oauth1.displayName().value(), is("oauth_1_0"));
        assertThat(oauth1.description().value(), is("OAuth 1.0 continues to be supported"));
        assertThat(oauth1.type(), is("OAuth 1.0"));

        SecuritySchemeSettings settings = oauth1.settings();
        assertThat(settings.requestTokenUri().value(), is("https://api.dropbox.com/1/oauth/request_token"));
        assertThat(settings.authorizationUri().value(), is("https://www.dropbox.com/1/oauth/authorize"));
        assertThat(settings.tokenCredentialsUri().value(), is("https://api.dropbox.com/1/oauth/access_token"));

        assertThat(settings.signatures(), hasSize(2));
        assertThat(settings.signatures().get(0), is("HMAC-SHA1"));
        assertThat(settings.signatures().get(1), is("PLAINTEXT"));
    }

    private void assertOauth2SecurityScheme(SecurityScheme oauth2)
    {
        assertThat(oauth2.name(), is("oauth_2_0"));
        assertThat(oauth2.displayName().value(), is("OAuth2"));
        assertThat(oauth2.description().value(), is("oauth 2.0"));
        assertThat(oauth2.type(), is("OAuth 2.0"));
        SecuritySchemePart describedBy = oauth2.describedBy();

        List<TypeDeclaration> headers = describedBy.headers();
        assertThat(headers.size(), is(1));
        assertThat(headers.get(0).name(), is("Authorization"));
        assertThat(headers.get(0).type(), is("string"));

        List<TypeDeclaration> queryParameters = describedBy.queryParameters();
        assertThat(queryParameters.size(), is(1));
        assertThat(queryParameters.get(0).name(), is("access_token"));
        assertThat(queryParameters.get(0).type(), is("string"));

        List<Response> responses = describedBy.responses();
        assertThat(responses.size(), is(2));
        assertThat(responses.get(0).code().value(), is("401"));
        assertThat(responses.get(0).description().value(), containsString("Bad or expired token"));

        SecuritySchemeSettings settings = oauth2.settings();
        assertThat(settings.authorizationUri().value(), is("https://www.dropbox.com/1/oauth2/authorize"));
        assertThat(settings.accessTokenUri().value(), is("https://api.dropbox.com/1/oauth2/token"));
        assertThat(settings.authorizationGrants().size(), is(2));
        assertThat(settings.authorizationGrants().get(0), is("authorization_code"));
        assertThat(settings.authorizationGrants().get(1), is("implicit"));
    }

    private void assertBaseUriParameters(List<TypeDeclaration> baseUriParameters)
    {
        assertThat(baseUriParameters.size(), is(4));
        TypeDeclaration param1 = baseUriParameters.get(0);
        assertThat(param1.name(), is("param1"));
        assertThat(param1.displayName().value(), is("Param 1"));
        assertThat(param1.description().value(), is("some description"));
        assertThat(param1.type(), is("string"));
        assertThat(param1.defaultValue(), nullValue());

        TypeDeclaration param2 = baseUriParameters.get(1);
        assertThat(param2.name(), is("param2"));
        assertThat(param2.required(), is(false));
        assertThat(param2.type(), is("string"));

        TypeDeclaration param3 = baseUriParameters.get(2);
        assertThat(param3.name(), is("param3?"));
        assertThat(param3.required(), is(true));
        assertThat(param3.type(), is("string"));

        TypeDeclaration param4 = baseUriParameters.get(3);
        assertThat(param4.name(), is("param4?"));
        assertThat(param4.required(), is(false));
        assertThat(param4.type(), is("string"));

        assertBaseUriExample(param1.example());
        assertThat(param1.required(), is(true));

    }

    private void assertBaseUriExample(ExampleSpec example)
    {
        assertThat(example.value(), is("one"));
        assertThat(example.structuredValue().value().toString(), is("one"));
        assertThat(example.name(), nullValue());
    }

    private void assertDocumentation(List<DocumentationItem> documentation)
    {
        assertThat(documentation.size(), is(2));
        assertThat(documentation.get(0).title().value(), is("doc title 1"));
        assertThat(documentation.get(0).content().value(), is("single line"));
        assertThat(documentation.get(0).content().annotations(), hasSize(1));
        assertScalarAnnotation(documentation.get(0).content(), "first chapter");
        assertThat(documentation.get(1).title().value(), is("doc title 2"));
        assertThat(documentation.get(1).content().value(), is("multi\nline\n"));
    }

    private void assertScalarAnnotation(AnnotableStringType scalar, String value)
    {
        assertThat(scalar.annotations().get(0).structuredValue().value().toString(), is(value));
    }

    private void assertTraits(List<Trait> traits)
    {
        assertThat(traits.size(), is(2));
        assertThat(traits.get(0).name(), is("traitOne"));
        assertThat(traits.get(0).usage().value(), is("late night"));
    }

    private void assertResourceTypes(List<ResourceType> resourceTypes)
    {
        assertThat(resourceTypes.size(), is(1));
        assertThat(resourceTypes.get(0).usage().value(), is("first usage"));
    }

    private void assertResources(List<Resource> resources)
    {
        assertThat(resources.size(), is(1));
        Resource top = resources.get(0);
        assertThat(top.relativeUri().value(), is("/top"));
        assertThat(top.resourcePath(), is("/top"));
        assertThat(top.description().value(), is("top description"));
        assertThat(top.displayName().value(), is("/top"));
        assertMethods(top.methods());
        assertTraitsRefs(top.is());
        assertResourceTypeRef(top.type());

        List<Resource> children = top.resources();
        assertThat(children.size(), is(1));
        Resource child = children.get(0);
        assertThat(child.relativeUri().value(), is("/child/{childId}"));
        assertThat(child.resourcePath(), is("/top/child/{childId}"));
        assertThat(child.parentResource().resourcePath(), is("/top"));
        assertThat(child.uriParameters().get(0).name(), is("childId"));
        assertThat(child.uriParameters().get(0).required(), is(false));
        assertThat(child.uriParameters().get(1).name(), is("childId2?"));
        assertThat(child.uriParameters().get(1).required(), is(true));
        assertSecuredBy(child.securedBy());
        TypeDeclaration typeDeclaration = child.methods().get(0).body().get(0);
        assertThat(typeDeclaration.name(), is("application/json"));
        assertThat(typeDeclaration.type(), is("UserJson"));
    }

    private void assertResourceTypeRef(ResourceTypeRef resourceTypeRef)
    {
        assertThat(resourceTypeRef.name(), is("first"));
        assertThat(resourceTypeRef.resourceType().usage().value(), is("first usage"));
    }

    private void assertTraitsRefs(List<TraitRef> traitRefs)
    {
        assertThat(traitRefs.size(), is(2));
        assertThat(traitRefs.get(0).name(), is("traitOne"));

        assertThat(traitRefs.get(1).name(), is("traitTwo"));
        TypeInstance param = traitRefs.get(1).structuredValue();
        assertThat(param.properties().size(), is(1));
        assertThat(param.properties().get(0).name(), is("text"));
        assertThat(param.properties().get(0).value().isScalar(), is(true));
        assertThat(param.properties().get(0).value().value().toString(), is("hola"));

    }

    private void assertMethods(List<Method> methods)
    {
        assertThat(methods.size(), is(2));
        Method get = methods.get(0);
        assertThat(get.method(), is("get"));
        assertThat(get.resource().relativeUri().value(), is("/top"));
        assertThat(get.description().value(), is("get something"));
        assertThat(get.displayName().value(), is("uno"));
        assertThat(get.protocols().size(), is(1));
        assertThat(get.protocols().get(0), is("HTTPS"));
        assertThat(get.securedBy().size(), is(1));
        assertThat(get.securedBy().get(0).name(), is("oauth_2_0"));
        assertQueryParameters(get.queryParameters());
        assertHeaders(get.headers());

        Method post = methods.get(1);
        assertThat(post.method(), is("post"));
        assertBody(post.body());
        assertResponses(post.responses());
        assertThat(post.is().size(), is(1));
        assertThat(post.is().get(0).name(), is("traitTwo"));
        assertThat(post.queryString().type(), is("object"));
    }

    private void assertQueryParameters(List<TypeDeclaration> queryParameters)
    {
        assertThat(queryParameters.size(), is(6));
        assertThat(queryParameters.get(0).validate("10").size(), is(0));
        assertThat(queryParameters.get(0).validate("10feet").size(), is(1));
        ArrayTypeDeclaration arrayType = (ArrayTypeDeclaration) queryParameters.get(1);
        assertThat(arrayType.validate("- a\n- 2\n").size(), is(0));
        assertTrue(arrayType.items() instanceof UnionTypeDeclaration);

        assertThat(queryParameters.get(2).name(), is("three"));
        assertThat(queryParameters.get(2).type(), is("integer"));
        assertThat(queryParameters.get(2).required(), is(false));

        assertThat(queryParameters.get(3).name(), is("four"));
        assertThat(queryParameters.get(3).type(), is("integer"));
        assertThat(queryParameters.get(3).required(), is(false));

        assertThat(queryParameters.get(4).name(), is("five?"));
        assertThat(queryParameters.get(4).type(), is("string"));
        assertThat(queryParameters.get(4).required(), is(true));

        assertThat(queryParameters.get(5).name(), is("six?"));
        assertThat(queryParameters.get(5).type(), is("string"));
        assertThat(queryParameters.get(5).required(), is(false));
    }

    private void assertHeaders(List<TypeDeclaration> headers)
    {
        assertThat(headers.size(), is(2));
        assertThat(headers.get(0).name(), is("one"));
        assertThat(headers.get(0).validate("ten"), empty());
        assertThat(headers.get(0).validate("10"), empty());
        assertThat(headers.get(0).validate("{}"), empty());
        assertThat(headers.get(1).displayName().value(), is("The Second"));
    }

    private void assertResponses(List<Response> responses)
    {
        assertThat(responses.size(), is(2));
        Response response200 = responses.get(0);
        assertThat(response200.code().value(), is("200"));
        assertBody(response200.body());
        assertHeaders(response200.headers());

        Response response400 = responses.get(1);
        assertThat(response400.code().value(), is("400"));
        assertThat(response400.body(), hasSize(1));
        assertThat(response400.body().get(0).name(), is("text/plain"));
    }

    private void assertBody(List<TypeDeclaration> body)
    {
        assertThat(body.size(), is(2));

        TypeDeclaration appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        String jsonExample = appJson.example().value();
        assertThat(jsonExample, containsString("\"firstname\": \"tato\""));
        assertThat(appJson.type(), is("User"));
        List<ValidationResult> validationResults = appJson.validate(jsonExample);
        assertThat(validationResults.size(), is(0));

        TypeDeclaration appXml = body.get(1);
        assertThat(appXml.name(), is("application/xml"));
        assertThat(appXml.examples().size(), is(2));
        assertThat(appXml.examples().get(0).value(), is("<first/>\n"));
        String xsd = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n"
                     + "<xsd:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" version=\"1.0\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n"
                     + "  <xsd:element name=\"first\" type=\"xsd:string\" />\n" + "  <xsd:element name=\"second\" type=\"xsd:string\" />\n" + "</xsd:schema>\n";
        assertThat(appXml.type(), is(xsd));
        assertThat(((XMLTypeDeclaration) appXml).schemaContent(), is(xsd));
    }
}
