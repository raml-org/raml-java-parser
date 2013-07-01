
package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.SimpleRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RuleNodeHandler;

public class TitleRuleTestCase
{

    @Test
    public void testTitleNotEmpty()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title:";
        RuleNodeHandler ramlValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is(SimpleRule.getRuleEmptyMessage("title")));
    }

    @Test
    public void testTitlePresent()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n";
        RuleNodeHandler ramlValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());

        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is(SimpleRule.getMissingRuleMessage("title")));
    }

    @Test
    public void testTitleNotMoreThanOnce()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: bla \n" + "title: bla";
        RuleNodeHandler ramlValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is(SimpleRule.getDuplicateRuleMessage("title")));
    }

}
