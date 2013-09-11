package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.Resource;

public class ResourceTypesTestCase extends AbstractBuilderTestCase
{

    @Test
    public void parse()
    {
        Raml raml = this.parseRaml("org/raml/types/resource-types.yaml");
        Resource users = raml.getResources().get("/users");
        assertThat(users.getActions().size(), is(0));
    }

}
