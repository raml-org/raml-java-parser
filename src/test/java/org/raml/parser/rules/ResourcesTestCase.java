package org.raml.parser.rules;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class ResourcesTestCase
{

    @Test
    public void resourceURIOk() throws IOException
    {
        final String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-with-uri.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

    @Test
    public void resourceDescriptionOk() throws IOException
    {
        final String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-with-description-ok.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

    @Test
    public void resourceFullOk() throws IOException
    {
        final String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/rules/resource-full-ok.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

}
