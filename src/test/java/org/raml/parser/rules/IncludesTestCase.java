package org.raml.parser.rules;

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

public class IncludesTestCase
{

    @Test
    public void include()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n"
                      + "title: !include org/raml/parser/rules/title.txt\n"
                      + "baseUri: https://{communityDomain}.force.com/";

        YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(raml);
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }
}
