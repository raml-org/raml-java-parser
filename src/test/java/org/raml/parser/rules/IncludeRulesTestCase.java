package org.raml.parser.rules;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class IncludeRulesTestCase extends AbstractRamlTestCase
{

    @Test
    public void include()
    {
        validateRamlNoErrors("org/raml/parser/rules/includes.yaml");
    }

    @Test
    public void includeNotFound()
    {
        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includes-bad.yaml");
        assertThat("Errors are not 1 " + errors, errors.size(), CoreMatchers.is(1));
        assertThat(errors.get(0).getMessage(), CoreMatchers.is("Include can not be resolved org/raml/parser/rules/title2.txt"));
    }

    @Test
    public void includeWithError()
    {
        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includes-yaml-with-error.yaml");
        assertThat("Errors are not 1 " + errors, errors.size(), CoreMatchers.is(1));
        assertThat(errors.get(0).getMessage(), containsString("Unknown key: invalid"));
        assertThat(errors.get(0).getIncludeName(), containsString("org/raml/parser/rules/included-with-error.yaml"));
    }
}
