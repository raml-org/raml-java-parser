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
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.raml.v2.api.model.v08.api.Api;
import org.raml.v2.api.model.v08.api.DocumentationItem;
import org.raml.v2.api.model.v08.bodies.BodyLike;
import org.raml.v2.api.model.v08.bodies.Response;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.methods.Trait;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v08.parameters.StringTypeDeclaration;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.resources.ResourceType;

public class SpecInterfacesV08TestCase
{

    @Test
    public void full() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v08/full/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV08();
        assertApi(api);
    }

    private void assertApi(Api api)
    {
        assertThat(api.title(), is("api title"));
        assertThat(api.version(), is("v1"));
        assertThat(api.baseUri().value(), is("http://base.uri/{version}/{param1}"));
        assertThat(api.mediaType().value(), is("application/json"));
        assertThat(api.protocols().size(), is(2));
        assertThat(api.protocols().get(0), is("HTTP"));
        assertThat(api.protocols().get(1), is("HTTPS"));

        assertThat(api.schemas().size(), is(2));
        assertThat(api.schemas().get(0).key(), is("UserJson"));
        assertThat(api.schemas().get(0).value().value(), containsString("\"firstname\":  { \"type\": \"string\" }"));
        assertThat(api.schemas().get(1).key(), is("UserXml"));

        assertDocumentation(api.documentation());
        assertTraits(api.traits());
        assertResourceTypes(api.resourceTypes());
        assertResources(api.resources());
        assertApiBaseUriParameters(api.baseUriParameters());
    }

    private void assertApiBaseUriParameters(List<Parameter> parameters)
    {
        assertThat(parameters.size(), is(1));
        assertThat(parameters.get(0).name(), is("param1"));
        assertThat(parameters.get(0).description().value(), is("some description"));
        assertThat(parameters.get(0).type(), is("string"));
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
        assertThat(traits.get(0).name(), is("one"));
        assertThat(traits.get(0).description().value(), is("method description from trait one"));
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
        assertResourceBaseUriParameters(top.baseUriParameters());
        assertMethods(top.methods());

        List<Resource> children = top.resources();
        assertThat(children.size(), is(1));
        Resource child = children.get(0);
        assertThat(child.relativeUri().value(), is("/children"));
        assertThat(child.resourcePath(), is("/top/children"));
        assertThat(child.parentResource().resourcePath(), is("/top"));
        BodyLike childrenBody = children.get(0).methods().get(0).body().get(0);
        assertThat(childrenBody.name(), is("application/json"));
        assertThat(childrenBody.schemaContent(), containsString("\"firstname\":  { \"type\": \"string\" }"));
        assertThat(childrenBody.schema().value(), is("UserJson"));

        Resource childId = child.resources().get(0);
        assertThat(childId.uriParameters(), hasSize(1));
        assertThat(childId.uriParameters().get(0).name(), is("childId"));
        assertThat(childId.uriParameters().get(0).type(), is("string"));
        assertThat(childId.uriParameters().get(0).required(), is(true));
    }

    private void assertResourceBaseUriParameters(List<Parameter> parameters)
    {
        assertThat(parameters.size(), is(1));
        assertThat(parameters.get(0).name(), is("param1"));
        assertThat(parameters.get(0).description().value(), is("resource override"));
        assertThat(parameters.get(0).type(), is("number"));
    }


    private void assertMethods(List<Method> methods)
    {
        assertThat(methods.size(), is(2));
        Method get = methods.get(0);
        assertThat(get.description().value(), is("get something"));
        assertThat(get.method(), is("get"));
        assertThat(get.resource().relativeUri().value(), is("/top"));
        assertMethodBaseUriParameters(get.baseUriParameters());
        assertQueryParameters(get.queryParameters());

        Method post = methods.get(1);
        assertThat(post.method(), is("post"));
        assertBody(post.body());
        assertResponses(post.responses());
    }

    private void assertQueryParameters(List<Parameter> parameters)
    {
        assertThat(parameters, hasSize(1));
        Parameter order = parameters.get(0);
        assertThat(order.name(), is("order"));
        assertThat(order.type(), is("string"));
        assertThat(order.displayName(), is("order"));
        assertThat(order.example(), is("desc"));
        assertThat(order.defaultValue(), is("asc"));
        assertThat(order.required(), is(false));
        assertThat(order.repeat(), is(true));
        assertTrue(order instanceof StringTypeDeclaration);
        assertThat(((StringTypeDeclaration) order).maxLength(), is(4));
    }

    private void assertMethodBaseUriParameters(List<Parameter> parameters)
    {
        assertThat(parameters.size(), is(1));
        assertThat(parameters.get(0).name(), is("param1"));
        assertThat(parameters.get(0).description().value(), is("method override"));
        assertThat(parameters.get(0).type(), is("boolean"));
    }


    private void assertResponses(List<Response> responses)
    {
        assertThat(responses.size(), is(2));
        Response response200 = responses.get(0);
        assertThat(response200.code().value(), is("200"));
        assertBody(response200.body());
    }

    private void assertBody(List<BodyLike> body)
    {
        assertThat(body.size(), is(4));

        BodyLike appJson = body.get(0);
        assertThat(appJson.name(), is("application/json"));
        assertThat(appJson.example().value(), containsString("\"firstname\": \"tato\""));
        assertThat(appJson.schema().value(), is("UserJson"));
        assertThat(appJson.schemaContent(), containsString("\"firstname\":  { \"type\": \"string\" }"));

        BodyLike xml = body.get(1);
        assertThat(xml.name(), is("application/xml"));
        assertThat(xml.schema().value(), is("UserXml"));
        assertThat(xml.schemaContent(), containsString("<xs:element type=\"xs:string\" name=\"input\"/>"));

        BodyLike multipart = body.get(2);
        assertThat(multipart.formParameters().size(), is(2));
        assertThat(multipart.formParameters().get(0).name(), is("description"));

        BodyLike vndJson = body.get(3);
        assertThat(vndJson.name(), is("application/vnd.inline+json"));
        assertThat(vndJson.schema().value(), containsString("\"input\": {"));
        assertThat(vndJson.schemaContent(), containsString("\"input\": {"));
    }
}
