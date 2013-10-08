package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;

public class TemplateParamFunctionsTestCase extends AbstractBuilderTestCase
{

    private static final String ramlSource = "org/raml/types/template-param-functions.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void resourceTypeDefaultParams()
    {
        assertThat(raml.getResources().get("/users").getDescription(),
                   is("regular users, singular user, plural users"));
    }

    @Test
    public void resourceTypeCustomParams()
    {
        assertThat(raml.getResources().get("/tags").getDescription(),
                   is("irregular plural octopi, " +
                      "irregular singular foot, " +
                      "regular plural dresses, " +
                      "regular singular stress"));
    }

    @Test
    public void validate()
    {
        validateRamlNoErrors(ramlSource);
    }
}
