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
package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;

public class SecurityTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/security.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void build()
    {
        assertThat(raml.getSecuritySchemes().size(), is(2));
    }

    @Test
    public void validate()
    {
        List<ValidationResult> errors = validateRaml(ramlSource);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

}
