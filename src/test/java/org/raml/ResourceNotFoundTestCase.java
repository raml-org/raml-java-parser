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
package org.raml;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.loader.ResourceNotFoundException;
import org.raml.parser.rule.ValidationResult;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class ResourceNotFoundTestCase extends AbstractRamlTestCase
{

    @Test(expected = ResourceNotFoundException.class)
    public void resourceNotFoundOnParsing()
    {
        parseRaml("invalid/raml/resource.raml");
    }

    @Test
    public void resourceNotFoundOnValidation()
    {
        List<ValidationResult> results = validateRaml("invalid/raml/resource.raml");
        assertThat(results.size(), is(1));
        assertThat(results.get(0).getMessage(), is("RAML resource not found"));
    }

}
