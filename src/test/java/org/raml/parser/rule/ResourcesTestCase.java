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
package org.raml.parser.rule;

import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class ResourcesTestCase extends AbstractRamlTestCase
{

    @Test
    public void resourceURIOk() throws IOException
    {
        String location = "org/raml/rules/resource-with-uri.yaml";
        List<ValidationResult> errors = validateRaml(location);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

    @Test
    public void resourceDescriptionOk() throws IOException
    {
        String location = "org/raml/rules/resource-with-description-ok.yaml";
        List<ValidationResult> errors = validateRaml(location);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

    @Test
    public void resourceFullOk() throws IOException
    {
        String location = "org/raml/rules/resource-full-ok.yaml";
        List<ValidationResult> errors = validateRaml(location);
        Assert.assertTrue("Errors must be empty but is : " + errors.size() + " -> " + errors, errors.isEmpty());
    }

}
