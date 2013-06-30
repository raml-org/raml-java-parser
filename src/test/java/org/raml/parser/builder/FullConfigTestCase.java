package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.Resource;
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
        String baseUri = "https://{host}.sample.com/{path}";
        assertThat(raml.getBaseUri(), is(baseUri));

        //uri parameters
        assertThat(raml.getUriParameters().size(), is(2));

        UriParameter hostParam = raml.getUriParameters().get("host");
        assertThat(hostParam.getName(), is("Host"));
        assertThat(hostParam.getDescription(), is("host name"));
        assertThat(hostParam.getMinLength(), is(5));
        assertThat(hostParam.getMaxLength(), is(10));
        assertThat(hostParam.getPattern(), is("[a-z]*"));

        assertThat(hostParam.getType(), is(ParamType.STRING));
        UriParameter pathParam = raml.getUriParameters().get("path");
        assertThat(pathParam.getName(), is("Path"));
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
        assertThat(action.getName(), is("retrieve"));
        assertThat(action.getBody().size(), is(1));
        assertThat(action.getBody().get("application/json"), CoreMatchers.notNullValue());

        //nested resource
        assertThat(mediaResource.getResources().size(), is(1));
        String mediaItemUri = "/{mediaId}";
        Resource mediaItemResource = mediaResource.getResource(mediaItemUri);
        assertThat(mediaItemResource.getRelativeUri(), is(mediaItemUri));
        assertThat(mediaItemResource.getName(), is("Media item"));
        assertThat(mediaItemResource.getActions().size(), is(1));

    }
}
