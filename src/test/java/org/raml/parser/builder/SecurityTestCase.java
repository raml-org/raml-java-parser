package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;

public class SecurityTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/security.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void build()
    {
        assertThat(raml.getSecuritySchemes().size(), is(2));
    }

    @Test
    public void validate()
    {
        List<ValidationResult> errors = validateRaml(ramlSource);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

}
