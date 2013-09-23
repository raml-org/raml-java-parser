package org.raml;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.rule.SimpleRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;


public class TitleTestCase
{

    @Test
    public void whenTitleIsNotDefinedErrorShouldBeShown() throws IOException
    {
        String raml = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("org/raml/title-not-defined.yaml"), "UTF-8");
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is(SimpleRule.getMissingRuleMessage("title")));
    }

}
