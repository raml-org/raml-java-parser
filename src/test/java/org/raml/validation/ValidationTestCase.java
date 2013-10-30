package org.raml.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class ValidationTestCase extends AbstractRamlTestCase
{

    @Test
    public void sequenceExpected()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/sequence-expected.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Sequence expected"));
    }

    @Test
    public void invalidCustomTag()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/invalid-tag.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Unknown tag !import"));
    }

}
