package org.raml.tagresolver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class PojoValidatorTestCase extends AbstractRamlTestCase
{

    @Test
    public void pojoNotFound()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/tagresolver/pojo-validator-not-found.yaml");
        assertThat(validationResults.size(), is(2));
        assertThat(validationResults.get(0).getMessage(), containsString("Class not found org.raml.tagresolver.user"));
        assertThat(validationResults.get(1).getMessage(), containsString("Class not found org.raml.tagresolver.Users"));
    }

    @Test
    public void pojoFound()
    {
        validateRamlNoErrors("org/raml/tagresolver/pojo-validator-found.yaml");
    }
}
