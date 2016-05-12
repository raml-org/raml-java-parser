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
package org.raml.v2.parser;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.api.DocumentationItem;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.datamodel.ExampleSpec;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeInstance;
import org.raml.v2.api.model.v10.datamodel.TypeInstanceProperty;
import org.raml.v2.api.model.v10.declarations.AnnotationRef;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.Trait;
import org.raml.v2.api.model.v10.methods.TraitRef;
import org.raml.v2.api.model.v10.resources.Resource;
import org.raml.v2.api.model.v10.resources.ResourceType;
import org.raml.v2.api.model.v10.resources.ResourceTypeRef;
import org.raml.v2.api.model.v10.security.AbstractSecurityScheme;
import org.raml.v2.api.model.v10.security.SecuritySchemePart;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;
import org.raml.v2.api.model.v10.security.SecuritySchemeSettings;

public class SpecInterfacesV10TestCase
{

    @Test
    public void full() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/interfaces/inputV10.raml");
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
        assertThat(basic.type().get(0), is("string"));
        TypeDeclaration hipermedia = annotationTypes.get(1);
        assertThat(hipermedia.name(), is("complex"));
    }

    private void assertApi(Api api)
    {
        assertThat(api.title(), is("api title"));
        assertThat(api.version(), is("v1"));
        assertThat(api.baseUri().value(), is("http://base.uri/{version}/{param1}"));
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
        assertThat(api.types().size(), is(1));
        assertThat(api.types().get(0).name(), is("User"));
        assertTraits(api.traits());
        assertResourceTypes(api.resourceTypes());
        assertAnnotationTypes(api.annotationTypes());
        assertSecuritySchemes(api.securitySchemes());
        assertAnnotations(api.annotations());
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
        assertThat(basic.annotation().type().get(0), is("string"));

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

    private void assertSecuritySchemes(List<AbstractSecurityScheme> securitySchemes)
    {
        assertThat(securitySchemes.size(), is(2));
        assertOauth2SecurityScheme(securitySchemes.get(0));
    }

    private void assertSecuredBy(List<SecuritySchemeRef> securedBy)
    {
        assertThat(securedBy.size(), is(2));
        assertThat(securedBy.get(0).name(), is("oauth_2_0"));
        assertOauth2SecurityScheme(securedBy.get(0).securityScheme());

        SecuritySchemeRef noSecurity = securedBy.get(1);
        assertThat(noSecurity.name(), is("null"));
        assertThat(noSecurity.securityScheme(), nullValue());
    }

    private void assertOauth2SecurityScheme(AbstractSecurityScheme oauth2)
    {
        assertThat(oauth2.name(), is("oauth_2_0"));
        assertThat(oauth2.displayName(), is("OAuth2"));
        assertThat(oauth2.description().value(), is("oauth 2.0"));
        assertThat(oauth2.type(), is("OAuth 2.0"));
        SecuritySchemePart describedBy = oauth2.describedBy();

        List<TypeDeclaration> headers = describedBy.headers();
        assertThat(headers.size(), is(1));
        assertThat(headers.get(0).name(), is("Authorization"));
        assertThat(headers.get(0).schemaContent(), is("string"));

        List<TypeDeclaration> queryParameters = describedBy.queryParameters();
        assertThat(queryParameters.size(), is(1));
        assertThat(queryParameters.get(0).name(), is("access_token"));
        assertThat(queryParameters.get(0).schemaContent(), is("string"));

        List<Response> responses = describedBy.responses();
        assertThat(responses.size(), is(2));
        assertThat(responses.get(0).code().value(), is("401"));
        assertThat(responses.get(0).description().value(), containsString("Bad or expired token"));

        SecuritySchemeSettings settings = oauth2.settings();
        assertThat(settings.authorizationUri().value(), is("https://www.dropbox.com/1/oauth2/authorize"));
        assertThat(settings.accessTokenUri().value(), is("https://api.dropbox.com/1/oauth2/token"));
        assertThat(settings.authorizationGrants().size(), is(2));
        assertThat(settings.authorizationGrants().get(0), is("authorization_code"));
        assertThat(settings.authorizationGrants().get(1), is("refresh_token"));
    }

    private void assertBaseUriParameters(List<TypeDeclaration> baseUriParameters)
    {
        assertThat(baseUriParameters.size(), is(1));
        TypeDeclaration param1 = baseUriParameters.get(0);
        assertThat(param1.name(), is("param1"));
        assertThat(param1.displayName(), is("Param 1"));
        assertThat(param1.description().value(), is("some description"));
        assertThat(param1.type().get(0), is("string"));
        assertThat(param1.defaultValue(), nullValue());
        assertBaseUriExample(param1.example());
        assertThat(param1.required(), is(true));

    }

    private void assertBaseUriExample(ExampleSpec example)
    {
        assertThat(example.value(), is("one"));
        assertThat(example.name(), nullValue());
    }

    private void assertDocumentation(List<DocumentationItem> documentation)
    {
        assertThat(documentation.size(), is(2));
        assertThat(documentation.get(0).title(), is("doc title 1"));
        assertThat(documentation.get(0).content().value(), is("single line"));
        assertThat(documentation.get(1).title(), is("doc title 2"));
        assertThat(documentation.get(1).content().value(), is("multi\nline\n"));
    }

    private void assertTraits(List<Trait> traits)
    {
        assertThat(traits.size(), is(2));
        assertThat(traits.get(0).displayName(), is("uno"));
        assertThat(traits.get(0).description().value(), is("method description"));
        assertThat(traits.get(0).usage(), is("late night"));
    }

    private void assertResourceTypes(List<ResourceType> resourceTypes)
    {
        assertThat(resourceTypes.size(), is(1));
        assertThat(resourceTypes.get(0).usage(), is("first usage"));
    }

    private void assertResources(List<Resource> resources)
    {
        assertThat(resources.size(), is(1));
        Resource top = resources.get(0);
        assertThat(top.relativeUri().value(), is("/top"));
        assertThat(top.resourcePath(), is("/top"));
        assertThat(top.description().value(), is("top description"));
        assertThat(top.displayName(), is("/top"));
        assertMethods(top.methods());
        assertTraitsRefs(top.is());
        assertResourceTypeRef(top.type());

        List<Resource> children = top.resources();
        assertThat(children.size(), is(1));
        Resource child = children.get(0);
        assertThat(child.relativeUri().value(), is("/child"));
        assertThat(child.resourcePath(), is("/top/child"));
        assertThat(child.parentResource().resourcePath(), is("/top"));
        assertSecuredBy(child.securedBy());
    }

    private void assertResourceTypeRef(ResourceTypeRef resourceTypeRef)
    {
        assertThat(resourceTypeRef.name(), is("first"));
        assertThat(resourceTypeRef.resourceType().usage(), is("first usage"));
    }

    private void assertTraitsRefs(List<TraitRef> traitRefs)
    {
        assertThat(traitRefs.size(), is(2));
        assertThat(traitRefs.get(0).name(), is("one"));
        assertThat(traitRefs.get(0).trait().description().value(), is("method description"));

        assertThat(traitRefs.get(1).name(), is("two"));
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
        assertThat(get.displayName(), is("get"));
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
        assertThat(post.is().get(0).name(), is("two"));
        assertThat(post.queryString().type().get(0), is("object"));
    }

    private void assertQueryParameters(List<TypeDeclaration> queryParameters)
    {
        assertThat(queryParameters.size(), is(2));
        assertThat(queryParameters.get(0).validate("10").size(), is(0));
        assertThat(queryParameters.get(0).validate("10feet").size(), is(1));
    }

    private void assertHeaders(List<TypeDeclaration> headers)
    {
        assertThat(headers.size(), is(2));
        assertThat(headers.get(0).name(), is("one"));
        assertThat(headers.get(1).displayName(), is("The Second"));
    }

    private void assertResponses(List<Response> responses)
    {
        assertThat(responses.size(), is(2));
        Response response200 = responses.get(0);
        assertThat(response200.code().value(), is("200"));
        assertBody(response200.body());
        assertHeaders(response200.headers());
    }

    private void assertBody(List<TypeDeclaration> body)
    {
        assertThat(body.size(), is(2));

        TypeDeclaration appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        String jsonExample = appJson.example().value();
        assertThat(jsonExample, containsString("\"firstname\": \"tato\""));
        assertThat(appJson.type().size(), is(1));
        assertThat(appJson.type().get(0), is("User"));
        List<ValidationResult> validationResults = appJson.validate(jsonExample);
        assertThat(validationResults.size(), is(0));

        TypeDeclaration appXml = body.get(1);
        assertThat(appXml.name(), is("application/xml"));
        assertThat(appXml.examples().size(), is(2));
        assertThat(appXml.examples().get(0).value(), is("<first/>\n"));
        String xsd = "<?xml version=\"1.0\" encoding=\"utf-16\"?>\n" +
                     "<xsd:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" version=\"1.0\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">\n" +
                     "  <xsd:element name=\"first\" type=\"xsd:string\" />\n" +
                     "  <xsd:element name=\"second\" type=\"xsd:string\" />\n" +
                     "</xsd:schema>\n";
        assertThat(appXml.schema().size(), is(1));
        assertThat(appXml.schema().get(0), is(xsd));
        assertThat(appXml.schemaContent(), is(xsd));
    }
}
