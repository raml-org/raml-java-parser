/**
 * 
 */
package org.raml.parser.rules;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.YamlDocumentValidator;

/**
 * @author Sebastian Sampaoli
 *
 */
public class UriParametersRuleTestCase
{
    @Test
    public void testUriParams() {
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
    public void testMinLenghtParameterNotValid() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: integer\n" + "   minLength: 35";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type string"));
    }

    @Test
    public void testMinLenght() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   minLength:";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minLength can not be empty"));
    }

    @Test
    public void testTypeMustExistBeforeMinLenghtExists() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   minLength: 32";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }

    @Test
    public void testVersionHaveNotToExistInUriParameters() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + " version: v3";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("'version' can not be declared, it is a reserved URI parameter."));
    }
    
    @Test
    public void testMaxLenght() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   maxLength:";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maxLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maxLength can not be empty"));
    }
    
    @Test
    public void testMinimum() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: number\n" + "   minimum:";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minimum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minimum can not be empty"));
    }
    
    @Test
    public void testMaximum() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: integer\n" + "   maximum:";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maximum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maximum can not be empty"));
    }
    
    @Test
    public void testMinimumNotValid() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   minimum: 35";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type integer or number"));
    }
    
    @Test
    public void testTypeMustExistBeforeMinimumtExists() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   minimum: 32";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type integer or number"));
    }

    @Test
    public void testTypeMustExistBeforeMaxLenghtExists() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   maxLength: 32";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }
    
    @Test
    public void testUriParameters() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/{param1}\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }
    
    @Test
    public void testRequiredFieldNotValid() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   required: 'o'";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("Type mismatch: required must be of type Boolean"));
    }
    
    @Test
    public void testRequiredFieldValid() {
        String simpleTest = "%TAG ! tag:raml.org,0.1:\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                + "uriParameters:\n" + " param2:\n" + "   name: Community Domain\n" + "   type: string\n" + "   required: 'y'";
        YamlDocumentValidator ramlSpecValidator = new YamlDocumentValidator(Raml.class);
        List<ValidationResult> errors = ramlSpecValidator.validate(simpleTest);
        Assert.assertTrue("Errors must not be empty", errors.isEmpty());
    }
}
