package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;

public class MinimalConfigTestCase extends AbstractRamlTestCase
{

    @Test
    public void basicConfig()
    {
        Raml raml = parseRaml("org/raml/root-elements.yaml");

        assertThat(raml.getTitle(), is("Sample API"));
        assertThat(raml.getVersion(), is("v1"));
        assertThat(raml.getBaseUri(), is("https://sample.com/api"));

        assertThat(raml.getResources().size(), is(1));
        Resource mediaResource = raml.getResources().get("/media");
        assertThat(mediaResource.getActions().size(), is(1));
        assertThat(mediaResource.getAction(ActionType.GET), is(Action.class));
    }

}
