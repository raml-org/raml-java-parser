package org.raml;

import java.util.List;

import org.raml.model.Raml;
import org.raml.parser.rule.BaseUriRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RuleNodeHandler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class BaseUriTestCase
{

    @Test
    public void testBaseURINotEmpty()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n" + "baseUri:";
        RuleNodeHandler havenSpecValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("The baseUri element is not a valid URI"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is(BaseUriRule.getRuleEmptyMessage("baseUri")));
    }

    @Test
    public void testBaseURIPresent()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle";
        RuleNodeHandler havenSpecValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is(BaseUriRule.getMissingRuleMessage("baseUri")));
    }

    @Test
    public void testBaseURIisNotValid()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n" + "title: apiTitle\n" + "baseUri: notavaliduri.com";
        RuleNodeHandler havenSpecValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = havenSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is(BaseUriRule.URI_NOT_VALID_MESSAGE));
    }

}
