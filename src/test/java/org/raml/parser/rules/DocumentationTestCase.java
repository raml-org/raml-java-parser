package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

public class DocumentationTestCase
{

    @Test
    public void documentation() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }

    @Test
    public void missingContent() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation-nocontent.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertThat(1, is(errors.size()));
        assertThat(errors.get(0).getMessage(), containsString("content is missing"));
    }

    @Test
    public void missingTitle() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation-notitle.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertThat(1, is(errors.size()));
        assertThat(errors.get(0).getMessage(), containsString("title is missing"));
    }

}
