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

public class UnknownRuleTestCase extends AbstractRamlTestCase
{

    @Test
    public void unknownElementsMustFailed()
    {
        String raml = "#%RAML 0.8\n" + "---\n"
                      + "title: Salesforce Chatter Communities REST API\n"
                      + "no-Title: Salesforce Chatter Communities REST API\n"
                      + "noBaseUri: Salesforce Chatter Communities REST API\n"
                      + "baseUri: https://{param2}.force.com/param\n" + "uriParameters:\n"
                      + " param2:\n" + "   name: Community Domain\n" + "   type: string\n"
                      + "   required: 'y'";
        List<ValidationResult> errors = validateRaml(raml, "");
        Assert.assertThat(errors.get(0).getMessage(),
                          CoreMatchers.is("Unknown key: no-Title"));
        Assert.assertThat(errors.get(1).getMessage(),
                          CoreMatchers.is("Unknown key: noBaseUri"));
    }
}
