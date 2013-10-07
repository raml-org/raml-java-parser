package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.GET;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;

public class TemplateDefaultParamsTestCase extends AbstractBuilderTestCase
{

    private static final String ramlSource = "org/raml/types/template-default-params.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void resourceTypeDefaultParams()
    {
        assertThat(raml.getResources().get("/simple").getDescription(),
                   is("resourcePath /simple, resourcePathName simple"));
    }

    @Test
    public void traitDefaultParams()
    {
        assertThat(raml.getResources().get("/simple").getAction(GET).getDescription(),
                   is("resourcePath /simple, resourcePathName simple, methodName get"));
    }

    @Test
    public void validate()
    {
        validateRamlNoErrors(ramlSource);
    }
}
