package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Response;
import org.raml.model.DocumentationItem;
import org.raml.model.MimeType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class FullConfigTestCase
{

    @Test
    public void fullConfig() throws Exception
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/full-config.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

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
        assertThat(raml.getBaseUri(), is(baseUri));

        //uri parameters
        assertThat(raml.getUriParameters().size(), is(3));

        UriParameter hostParam = raml.getUriParameters().get("host");
        assertThat(hostParam.getName(), is("Host"));
        assertThat(hostParam.getDescription(), is("host name"));
        assertThat(hostParam.getType(), is(ParamType.STRING));
        assertThat(hostParam.getMinLength(), is(5));
        assertThat(hostParam.getMaxLength(), is(10));
        assertThat(hostParam.getPattern(), is("[a-z]*"));

        UriParameter portParam = raml.getUriParameters().get("port");
        assertThat(portParam.getType(), is(ParamType.INTEGER));
        assertThat(portParam.getMinimum(), is(1025d));
        assertThat(portParam.getMaximum(), is(65535d));

        assertThat(hostParam.getType(), is(ParamType.STRING));
        UriParameter pathParam = raml.getUriParameters().get("path");
        assertThat(pathParam.getType(), is(ParamType.STRING));
        assertThat(pathParam.getEnumeration().size(), is(3));
        assertThat(pathParam.getEnumeration().get(0), is("one"));
        assertThat(pathParam.getEnumeration().get(1), is("two"));
        assertThat(pathParam.getEnumeration().get(2), is("three"));

        //resources
        assertThat(raml.getResources().size(), is(3));

        String rootUri = "/";
        Resource rootResource = raml.getResources().get(rootUri);
        assertThat(rootResource.getRelativeUri(), is(rootUri));
        assertThat(rootResource.getUri(), is(baseUri + rootUri));
        assertThat(rootResource.getName(), is("Root resource"));

        String mediaUri = "/media";
        Resource mediaResource = raml.getResources().get(mediaUri);
        assertThat(mediaResource.getRelativeUri(), is(mediaUri));
        assertThat(mediaResource.getUri(), is(baseUri + mediaUri));
        assertThat(mediaResource.getName(), is("Media collection"));

        //actions
        assertThat(mediaResource.getActions().size(), is(2));
        Action action = mediaResource.getAction(ActionType.GET);
        assertThat(action.getType(), is(ActionType.GET));
        assertThat(action.getSummary(), is("retrieve"));
        assertThat(action.getDescription(), is("retrieve media"));

        //action headers
        assertThat(action.getHeaders().size(), is(1));
        Header apiKeyHeader = action.getHeaders().get("api-key");
        assertThat(apiKeyHeader.getName(), is("Api key"));
        assertThat(apiKeyHeader.getDescription(), is("Api key description"));
        assertThat(apiKeyHeader.getType(), is(ParamType.STRING));
        assertThat(apiKeyHeader.isRequired(), is(true));
        assertThat(apiKeyHeader.getMinLength(), is(10));
        assertThat(apiKeyHeader.getMaxLength(), is(10));
        assertThat(apiKeyHeader.getExample(), is("0123456789"));

        //action query parameters
        assertThat(action.getQueryParameters().size(), is(1));
        QueryParameter pageQueryParam = action.getQueryParameters().get("page");
        assertThat(pageQueryParam.getType(), is(ParamType.INTEGER));
        assertThat(pageQueryParam.isRequired(), is(false));
        assertThat(pageQueryParam.getDefaultValue(), is("1"));
        assertThat(pageQueryParam.getMinimum(), is(1d));

        //action body types
        assertThat(action.getBody().size(), is(2));
        String jsonMime = "application/json";
        MimeType jsonBody = action.getBody().get(jsonMime);
        assertThat(jsonBody.getType(), is(jsonMime));
        assertThat(jsonBody.getSchema(), containsString("\"input\": {"));
        assertThat(jsonBody.getExample(), is("{ \"input\": \"hola\" }"));

        String formMime = "multipart/form-data";
        MimeType formBody = action.getBody().get(formMime);
        assertThat(formBody.getType(), is(formMime));
        assertThat(formBody.getParameters().size(), is(1));
        FormParameter form1Param = formBody.getParameters().get("form-1");
        assertThat(form1Param.getName(), is("form 1"));
        assertThat(form1Param.getDescription(), is("form 1 description"));
        assertThat(form1Param.getType(), is(ParamType.NUMBER));
        assertThat(form1Param.isRequired(), is(true));
        assertThat(form1Param.getMinimum(), closeTo(9.5, 0.01));
        assertThat(form1Param.getMaximum(), closeTo(10.5, 0.01));

        //action responses
        assertThat(action.getResponses().size(), is(2));
        Response response200 = action.getResponses().get("200");
        assertThat(response200.getBody().size(), is(1));
        assertThat(response200.getBody().get("application/json").getExample(), is("{ \"key\": \"value\" }"));
        Response response400 = action.getResponses().get("400");
        assertThat(response400.getBody().size(), is(1));
        assertThat(response400.getBody().get("text/xml").getExample(), is("<root>none</root>"));

        //nested resource
        assertThat(mediaResource.getResources().size(), is(1));
        String mediaItemUri = "/{mediaId}";
        Resource mediaItemResource = mediaResource.getResource(mediaItemUri);
        assertThat(mediaItemResource.getRelativeUri(), is(mediaItemUri));
        assertThat(mediaItemResource.getName(), is("Media item"));
        assertThat(mediaItemResource.getActions().size(), is(1));
        assertThat(mediaItemResource.getUriParameters().size(), is(1));
        UriParameter mediaIdParam = mediaItemResource.getUriParameters().get("mediaId");
        assertThat(mediaIdParam.getType(), is(ParamType.STRING));
        assertThat(mediaIdParam.getMaxLength(), is(10));

    }
}
