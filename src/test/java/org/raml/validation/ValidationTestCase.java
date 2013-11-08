/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.validation;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.parser.rule.ValidationMessage.NON_SCALAR_KEY_MESSAGE;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class ValidationTestCase extends AbstractRamlTestCase
{

    @Test
    public void sequenceExpected()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/sequence-expected.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Sequence expected"));
    }

    @Test
    public void invalidCustomTag()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/invalid-tag.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Unknown tag !import"));
    }


    @Test
    public void missingTemplate()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/missing-template.yaml");
        assertThat(validationResults.size(), is(2));
        assertThat(validationResults.get(0).getMessage(), is("trait not defined: paged"));
        assertThat(validationResults.get(1).getMessage(), is("resource type not defined: collection"));
    }

    @Test
    public void mapExpected()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/map-expected.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Mapping expected"));
    }

    @Test
    public void nonScalarKeys()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/non-scalar-keys.yaml");
        int expectedErrors = 6;
        assertThat(validationResults.size(), is(expectedErrors));
        for (int i = 0; i < expectedErrors; i++)
        {
            assertThat(validationResults.get(i).getMessage(), is(NON_SCALAR_KEY_MESSAGE));
        }
    }

    @Test
    public void invalidActionElement()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/invalid-action-element.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Invalid value type"));
    }

    @Test
    public void nonScalarGlobalSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/invalid-global-schema.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), is("Invalid value type"));
    }

    @Test
    public void duplicateMapEntry()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/validation/duplicate-map-entries.yaml");
        assertThat(validationResults.size(), is(3));
        assertThat(validationResults.get(0).getMessage(), is("Duplicate headers"));
        assertThat(validationResults.get(1).getMessage(), is("Duplicate actions"));
        assertThat(validationResults.get(2).getMessage(), is("Duplicate resources"));
    }
}
