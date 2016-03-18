/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.CONNECT;
import static org.raml.model.ParamType.INTEGER;
import static org.raml.model.ParamType.NUMBER;
import static org.raml.model.ParamType.STRING;
import static org.raml.model.Protocol.HTTP;
import static org.raml.model.Protocol.HTTPS;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.rule.ValidationResult;

public class FullConfigTestCase extends AbstractRamlTestCase
{

    private static String ramlSource = "org/raml/full-config.yaml";

    @Test
    public void validate()
    {
        List<ValidationResult> validationResults = validateRaml(ramlSource);
        assertThat(validationResults.size(), is(0));
    }

    @Test
    public void fullConfig()
    {
        Raml raml = parseRaml(ramlSource);

        //documentation
        List<DocumentationItem> documentation = raml.getDocumentation();
        assertThat(documentation.size(), is(2));
        assertThat(documentation.get(0).getTitle(), is("Home"));
        assertThat(documentation.get(0).getContent(), startsWith("Lorem ipsum dolor sit"));
        assertThat(documentation.get(1).getTitle(), is("section"));
        assertThat(documentation.get(1).getContent(), is("section content"));

        //basic attributes
        assertThat(raml.getTitle(), is("Sample API"));
        assertThat(raml.getVersion(), is("v1"));
        String baseUri = "https://{host}.sample.com:{port}/{path}";
        String basePath = "/{path}";
        assertThat(raml.getBaseUri(), is(baseUri));
        assertThat(raml.getBasePath(), is(basePath));
        assertThat(raml.getUri(), is(""));
        assertThat(raml.getProtocols().size(), is(2));
        assertThat(raml.getProtocols().get(0), is(HTTP));
        assertThat(raml.getProtocols().get(1), is(HTTPS));

        //uri parameters
        assertThat(raml.getBaseUriParameters().size(), is(3));
        Map<String, UriParameter> tagUriParameters = raml.getResource("/tags/{tagId}").getUriParameters();
        assertThat(tagUriParameters.size(), is(1));
        assertThat(tagUriParameters.get("tagId").getDisplayName(), is("tagId"));
        assertThat(tagUriParameters.get("tagId").isRequired(), is(true));
        assertThat(tagUriParameters.get("tagId").getType(), is(ParamType.STRING));

        UriParameter hostParam = raml.getBaseUriParameters().get("host");
        assertThat(hostParam.getDisplayName(), is("Host"));
        assertThat(hostParam.getDescription(), is("host name"));
        assertThat(hostParam.getType(), is(STRING));
        assertThat(hostParam.getMinLength(), is(5));
        assertThat(hostParam.getMaxLength(), is(10));
        assertThat(hostParam.getPattern(), is("[a-z]*"));

        UriParameter portParam = raml.getBaseUriParameters().get("port");
        assertThat(portParam.getType(), is(INTEGER));
        assertThat(portParam.getMinimum(), is(new BigDecimal(1025)));
        assertThat(portParam.getMaximum(), is(new BigDecimal(65535)));

        assertThat(hostParam.getType(), is(STRING));
        UriParameter pathParam = raml.getBaseUriParameters().get("path");
        assertThat(pathParam.getType(), is(STRING));
        assertThat(pathParam.getEnumeration().size(), is(3));
        assertThat(pathParam.getEnumeration().get(0), is("one"));
        assertThat(pathParam.getEnumeration().get(1), is("two"));
        assertThat(pathParam.getEnumeration().get(2), is("three"));

        //resources
        assertThat(raml.getResources().size(), is(3));

        String rootUri = "/";
        Resource rootResource = raml.getResources().get(rootUri);
        assertThat(rootResource.getRelativeUri(), is(rootUri));
        assertThat(rootResource.getUri(), is(rootUri));
        assertThat(rootResource.getDisplayName(), is("Root resource"));
        assertThat(rootResource.getDescription(), is("Root resource description"));
        assertThat(rootResource.getAction(ActionType.HEAD).getProtocols().size(), is(1));
        assertThat(rootResource.getAction(ActionType.HEAD).getProtocols().get(0), is(HTTP));

        String mediaUri = "/media";
        Resource mediaResource = raml.getResources().get(mediaUri);
        assertThat(mediaResource.getRelativeUri(), is(mediaUri));
        assertThat(mediaResource.getUri(), is(mediaUri));
        assertThat(mediaResource.getDisplayName(), is("Media collection"));

        //actions
        assertThat(mediaResource.getActions().size(), is(2));
        Action action = mediaResource.getAction(ActionType.GET);
        assertThat(action.getType(), is(ActionType.GET));
        assertThat(action.getDescription(), is("retrieve media"));
        assertThat(action.getDisplayName(), is("RetrieveMediaDisplayName"));

        //action headers
        assertThat(action.getHeaders().size(), is(1));
        Header apiKeyHeader = action.getHeaders().get("api-key");
        assertThat(apiKeyHeader.getDisplayName(), is("Api key"));
        assertThat(apiKeyHeader.getDescription(), is("Api key description"));
        assertThat(apiKeyHeader.getType(), is(STRING));
        assertThat(apiKeyHeader.isRequired(), is(true));
        assertThat(apiKeyHeader.getMinLength(), is(10));
        assertThat(apiKeyHeader.getMaxLength(), is(10));
        assertThat(apiKeyHeader.getExample(), is("0123456789"));

        //action query parameters
        assertThat(action.getQueryParameters().size(), is(1));
        QueryParameter pageQueryParam = action.getQueryParameters().get("page");
        assertThat(pageQueryParam.getType(), is(INTEGER));
        assertThat(pageQueryParam.isRequired(), is(false));
        assertThat(pageQueryParam.getDefaultValue(), is("1"));
        assertThat(pageQueryParam.getMinimum(), is(new BigDecimal(1)));

        //action body types
        assertThat(action.getBody().size(), is(3));
        String jsonMime = "application/json";
        MimeType jsonBody = action.getBody().get(jsonMime);
        assertThat(jsonBody.getType(), is(jsonMime));
        assertThat(jsonBody.getSchema(), containsString("\"input\": {"));
        assertThat(jsonBody.getExample(), is("{ \"input\": \"hola\" }"));

        String formMime = "multipart/form-data";
        MimeType formBody = action.getBody().get(formMime);
        assertThat(formBody.getType(), is(formMime));
        assertThat(formBody.getFormParameters().size(), is(2));
        List<FormParameter> form1Param = formBody.getFormParameters().get("form-1");
        assertThat(form1Param.get(0).getDisplayName(), is("form 1"));
        assertThat(form1Param.get(0).getDescription(), is("form 1 description"));
        assertThat(form1Param.get(0).getType(), is(NUMBER));
        assertThat(form1Param.get(0).isRequired(), is(true));
        assertThat(form1Param.get(0).getMinimum(), is(new BigDecimal("9.5")));
        assertThat(form1Param.get(0).getMaximum(), is(new BigDecimal("10.5")));
        assertThat(form1Param.get(1).getType(), is(STRING));
        assertThat(form1Param.get(1).getEnumeration().size(), is(3));

        //action responses
        assertThat(action.getResponses().size(), is(3));
        Response response200 = action.getResponses().get("200");
        assertThat(response200.getBody().size(), is(1));
        assertThat(response200.getBody().get("application/json").getExample(), is("{ \"key\": \"value\" }"));
        assertThat(response200.getHeaders().size(), is(2));
        Response response400 = action.getResponses().get("400");
        assertThat(response400.getBody().size(), is(2));
        assertThat(response400.getBody().get("text/xml").getExample(), is("<root>none</root>"));
        assertThat(response400.getBody().get("text/plain").getType(), is("text/plain"));
        Response response404 = action.getResponses().get("404");
        assertThat(response404.getDescription(), is("not found"));

        //nested resource
        assertThat(mediaResource.getResources().size(), is(1));
        String mediaItemUri = "/{mediaId}";
        Resource mediaItemResource = mediaResource.getResource(mediaItemUri);
        assertThat(mediaItemResource.getRelativeUri(), is(mediaItemUri));
        assertThat(mediaItemResource.getUri(), is(mediaUri + mediaItemUri));
        assertThat(mediaItemResource.getDisplayName(), is("Media item"));
        assertThat(mediaItemResource.getActions().size(), is(2));
        assertThat(mediaItemResource.getUriParameters().size(), is(1));
        UriParameter mediaIdParam = mediaItemResource.getUriParameters().get("mediaId");
        assertThat(mediaIdParam.getType(), is(STRING));
        assertThat(mediaIdParam.getMaxLength(), is(10));
        action = mediaItemResource.getAction(CONNECT);
        assertThat(action.getType(), is(CONNECT));
    }

}
