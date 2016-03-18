/*
 * Copyright 2016 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.parser.rules;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class UriParametersRuleTestCase extends AbstractRamlTestCase
{

    @Test
    public void testMinLenghtParameterNotValid()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: integer\n" + "   minLength: 35";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type string"));
    }

    @Test
    public void testMinLenght()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: string\n" + "   minLength:";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minLength can not be empty"));
    }

    @Test
    public void testTypeMustExistBeforeMinLenghtExists()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   minLength: 32";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }

    @Test
    public void testVersionHaveNotToExistInUriParameters()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + " version: v3";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("'version' can not be declared, it is a reserved URI parameter."));
    }

    @Test
    public void testMaxLenght()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: string\n" + "   maxLength:";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maxLength can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maxLength can not be empty"));
    }

    @Test
    public void testMinimum()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: number\n" + "   minimum:";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("minimum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("minimum can not be empty"));
    }

    @Test
    public void testMaximum()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: integer\n" + "   maximum:";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("maximum can only contain integer values greater than zero"));
        Assert.assertThat(errors.get(1).getMessage(), CoreMatchers.is("maximum can not be empty"));
    }

    @Test
    public void testMinimumNotValid()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: string\n" + "   minimum: 35";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must be of type integer or number"));
    }

    @Test
    public void testTypeMustExistBeforeMinimumtExists()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   minimum: 32";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type integer or number"));
    }

    @Test
    public void testTypeMustExistBeforeMaxLenghtExists()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   maxLength: 32";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("type must exist first, and it must be of type string"));
    }

    @Test
    public void testUriParameters()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/{param1}\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testRequiredFieldNotValid()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: string\n" + "   required: 'o'";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is("Type mismatch: required must be of type boolean"));
    }

    @Test
    public void testRequiredFieldValid()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: Salesforce Chatter Communities REST API\n" + "baseUri: https://{param2}.force.com/param\n"
                      + "baseUriParameters:\n" + " param2:\n" + "   displayName: Community Domain\n" + "   type: string\n" + "   required: 'y'";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertTrue("Errors must not be empty", errors.isEmpty());
    }
}
