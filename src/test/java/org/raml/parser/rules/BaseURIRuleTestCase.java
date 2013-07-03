/**
 * 
 */

package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
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
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is("The baseUri element is not a valid URI"));
        Assert.assertThat(errors.get(1).getMessage(),
            CoreMatchers.is(BaseUriRule.getRuleEmptyMessage("baseUri")));
    }

    @Test
    public void testBaseURIPresent()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        YamlDocumentValidator havenSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is(BaseUriRule.getMissingRuleMessage("baseUri")));
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n"
                            + "baseUri: notavaliduri.com";
        YamlDocumentValidator havenSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }
}
