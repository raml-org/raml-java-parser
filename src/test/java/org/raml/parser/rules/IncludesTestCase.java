package org.raml.parser.rules;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

public class IncludesTestCase
{

    @Test
    public void include() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/includes.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }

    @Test
    public void includeNotFound() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/includes-bad.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertThat("Errors are not 1 "+errors,errors.size(), CoreMatchers.is(1));
        assertThat(errors.get(0).getMessage(),CoreMatchers.is("Include can not be resolved org/raml/parser/rules/title2.txt"));
    }

}
