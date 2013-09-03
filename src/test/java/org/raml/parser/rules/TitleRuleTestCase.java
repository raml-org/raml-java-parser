
package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.SimpleRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlValidationService;

public class TitleRuleTestCase
{

    @Test
    public void testTitleNotEmpty()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is(SimpleRule.getRuleEmptyMessage("title")));
    }

    @Test
    public void testTitlePresent()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "version: v28.0\n";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is(SimpleRule.getMissingRuleMessage("title")));
    }

    @Test
    public void testTitleNotMoreThanOnce()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: bla \n" + "title: bla";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is(SimpleRule.getDuplicateRuleMessage("title")));
    }

}
