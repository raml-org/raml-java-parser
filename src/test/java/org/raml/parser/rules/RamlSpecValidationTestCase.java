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

public class RamlSpecValidationTestCase extends AbstractRamlTestCase
{

    @Test
    public void testShouldNotFail()
    {
        String raml = "#%RAML 0.8\n" + "---\n"
                      + "title: Salesforce Chatter Communities REST API\n" + "version: v28.0\n"
                      + "baseUri: https://{communityDomain}.force.com/{communityPath}";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

    @Test
    public void testVersionMustExistWhenIsDeclaredInBaseUri()
    {
        String raml = "#%RAML 0.8\n" + "---\n"
                      + "title: Salesforce Chatter Communities REST API\n"
                      + "baseUri: https://{communityDomain}.force.com/{version}";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is("version parameter must exist in the API definition"));
    }
}
