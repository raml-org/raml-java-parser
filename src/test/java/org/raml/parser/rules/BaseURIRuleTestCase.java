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
import org.raml.parser.visitor.YamlDocumentValidator;

public class BaseURIRuleTestCase
{

    @Test
    public void testBaseURINotEmpty()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                            + "baseUri:";
        YamlDocumentValidator havenSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(1).getMessage(), is("The baseUri element is not a valid URI"));
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.getRuleEmptyMessage("baseUri")));
    }

    @Test
    public void testBaseURIOptional()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        YamlDocumentValidator havenSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                            + "baseUri: notavaliduri.com";
        YamlDocumentValidator havenSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }
}
