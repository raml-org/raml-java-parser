package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.raml.parser.rule.BaseUriRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class BaseURIRuleTestCase
{

    @Test
    public void testBaseURINotEmpty()
    {
        String raml = "#%RAML 0.2\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri:";
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(1).getMessage(), is("The baseUri element is not a valid URI"));
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.getRuleEmptyMessage("baseUri")));
    }

    @Test
    public void testBaseURIOptional()
    {
        String raml = "#%RAML 0.2\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String raml = "#%RAML 0.2\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri: notavaliduri.com";
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }
}
