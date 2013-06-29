package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

public class MinimalConfigTestCase
{

    @Test
    public void basicConfig() throws Exception
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/root-elements.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

        assertThat(raml.getTitle(), is("Sample API"));
        assertThat(raml.getVersion(), is("v1"));
        assertThat(raml.getBaseUri(), is("https://sample.com/api"));

        assertThat(raml.getResources().size(), is(1));
        Resource mediaResource = raml.getResources().get("/media");
        assertThat(mediaResource.getActions().size(), is(1));
        assertThat(mediaResource.getAction(ActionType.GET), is(Action.class));
    }

}
