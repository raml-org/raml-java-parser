/**
 *
 */
package org.raml.parser.rules;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

public class ResourcesTestCase
{

    @Test
    public void resourceURIOk() throws IOException
    {
        final String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-with-uri.yaml"));
        final YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        final List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

    @Test
    public void resourceDescriptionOk() throws IOException
    {
        final String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-with-description-ok.yaml"));
        final YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        final List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }
    @Test
    public void resourceFullOk() throws IOException
    {
        final String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-full-ok.yaml"));
        final YamlDocumentValidator ramlValidator = new YamlDocumentValidator(Raml.class);
        final List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

}
