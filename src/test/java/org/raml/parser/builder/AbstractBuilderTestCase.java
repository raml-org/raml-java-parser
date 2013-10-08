package org.raml.parser.builder;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;

public class AbstractBuilderTestCase
{

    protected static Raml parseRaml(String resource)
    {
        return new RamlDocumentBuilder().build(getInputStream(resource));
    }

    protected static Raml parseRaml(String resource, RamlDocumentBuilder builder)
    {
        return builder.build(getInputStream(resource));
    }

    protected static List<ValidationResult> validateRaml(String resource)
    {
        return RamlValidationService.createDefault().validate(getString(resource));
    }

    protected static void validateRamlNoErrors(String resource)
    {
        List<ValidationResult> validationResults = validateRaml(resource);
        assertTrue("Errors must be empty", validationResults.isEmpty());
    }

    private static String getString(String resource)
    {
        try
        {
            return IOUtils.toString(getInputStream(resource));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(String resource)
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
