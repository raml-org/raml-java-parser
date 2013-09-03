package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.BaseUriRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlValidationService;

public class BaseURIRuleTestCase
{

    @Test
    public void testBaseURINotEmpty()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(1).getMessage(), is("The baseUri element is not a valid URI"));
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.getRuleEmptyMessage("baseUri")));
    }

    @Test
    public void testBaseURIOptional()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                      + "baseUri: notavaliduri.com";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }
}
