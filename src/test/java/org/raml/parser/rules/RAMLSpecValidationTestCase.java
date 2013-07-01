
package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RuleNodeHandler;

public class RAMLSpecValidationTestCase
{

    @Test
    public void testShouldNotFail()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n"
                            + "title: Salesforce Chatter Communities REST API\n" + "version: v28.0\n"
                            + "baseUri: https://{communityDomain}.force.com/{communityPath}";
        RuleNodeHandler ramlValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testVersionMustExistWhenIsDeclaredInBaseUri()
    {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n"
                            + "title: Salesforce Chatter Communities REST API\n"
                            + "baseUri: https://{communityDomain}.force.com/{version}";
        RuleNodeHandler ramlValidator = new RuleNodeHandler(Raml.class);
        List<ValidationResult> errors = ramlValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
            CoreMatchers.is("version parameter must exist in the API definition"));
    }
}
