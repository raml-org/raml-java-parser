package org.raml.parser.rules;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

public class DocumentationTestCase
{

    @Test @Ignore
    public void documentation() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation.yaml"));
        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }
}
