package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlValidationService;

public class UnknownRuleTestCase
{

    @Test
    public void unknownElementsMustFailed()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n"
                      + "title: Salesforce Chatter Communities REST API\n"
                      + "noTitle: Salesforce Chatter Communities REST API\n"
                      + "noBaseUri: Salesforce Chatter Communities REST API\n"
                      + "baseUri: https://{param2}.force.com/param\n" + "uriParameters:\n"
                      + " param2:\n" + "   name: Community Domain\n" + "   type: string\n"
                      + "   required: 'y'";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is("Unknown key: noTitle"));
        Assert.assertThat(errors.get(1).getMessage(),
                          CoreMatchers.is("Unknown key: noBaseUri"));
    }
}
