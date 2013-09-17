package org.raml.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.builder.AbstractBuilderTestCase;

public class SalesEnablementTestCase extends AbstractBuilderTestCase
{
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml("org/raml/integration/sales-enablement-api.yaml");
    }

    @Test
    public void schemas()
    {
        Map<String, String> schemas = raml.getSchemas();
        Action post = raml.getResources().get("/presentations").getAction(ActionType.POST);
        assertTrue(schemas.containsKey(post.getBody().get("application/json").getSchema()));
    }

    @Test
    public void presentations()
    {
        Resource simpleResource = raml.getResources().get("/presentations");
        assertThat(simpleResource.getActions().size(), is(2));
        Map<String,QueryParameter> queryParameters = simpleResource.getAction(ActionType.GET).getQueryParameters();
        assertThat(queryParameters.size(), is(3));
        assertThat(queryParameters.get("region").getDisplayName(), is("region"));
        assertThat(queryParameters.get("start").getDisplayName(), is("start"));
        assertThat(queryParameters.get("pages").getDisplayName(), is("pages"));
    }
}
