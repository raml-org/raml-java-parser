package org.raml.parser.rules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlValidationService;

public class UriParametersRuleTestCase
{

    @Test
    public void testUriParams()
    {
        String simpleUri = "https://{communityDomain}.force.com/{communityPath}";
        Pattern pattern = Pattern.compile("[.*]?\\{(\\w+)?\\}[.*]*");
        Matcher matcher = pattern.matcher(simpleUri);
        boolean find1 = matcher.find();
        Assert.assertTrue("URI must have parameters", find1);
        String paramValue = matcher.group(1);
        Assert.assertEquals("communityDomain", paramValue);
        boolean find2 = matcher.find();
        Assert.assertTrue("URI must have parameters", find2);
        String paramValue1 = matcher.group(1);
        Assert.assertEquals("communityPath", paramValue1);
    }

    @Test
    public void testMinLenghtParameterNotValid()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: integer\n" + "   minLength: 35";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type string"));
    }

    @Test
    public void testMinLenght()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   minLength:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minLength can not be empty"));
    }

    @Test
    public void testTypeMustExistBeforeMinLenghtExists()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   minLength: 32";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }

    @Test
    public void testVersionHaveNotToExistInUriParameters()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + " version: v3";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("'version' can not be declared, it is a reserved URI parameter."));
    }

    @Test
    public void testMaxLenght()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   maxLength:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maxLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maxLength can not be empty"));
    }

    @Test
    public void testMinimum()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: number\n" + "   minimum:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minimum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minimum can not be empty"));
    }

    @Test
    public void testMaximum()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: integer\n" + "   maximum:";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maximum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maximum can not be empty"));
    }

    @Test
    public void testMinimumNotValid()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   minimum: 35";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type integer or number"));
    }

    @Test
    public void testTypeMustExistBeforeMinimumtExists()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   minimum: 32";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type integer or number"));
    }

    @Test
    public void testTypeMustExistBeforeMaxLenghtExists()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   maxLength: 32";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }

    @Test
    public void testUriParameters()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/{param1}\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testRequiredFieldNotValid()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   required: 'o'";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("Type mismatch: required must be of type boolean"));
    }

    @Test
    public void testRequiredFieldValid()
    {
        String raml = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   required: 'y'";
        List<ValidationResult> errors = YamlValidationService.createDefault(Raml.class).validate(raml);
        Assert.assertTrue("Errors must not be empty", errors.isEmpty());
    }
}
