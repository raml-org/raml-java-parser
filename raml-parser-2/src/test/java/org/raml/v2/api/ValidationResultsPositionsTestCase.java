/*
 * Copyright 2013 (c) MuleSoft, Inc.
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
package org.raml.v2.api;

import org.junit.Test;
import org.raml.v2.api.model.common.ValidationResult;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ValidationResultsPositionsTestCase
{
    @Test
    public void errorPathTest() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/error-positions/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertTrue(ramlModelResult.hasErrors());

        assertErrorPath(ramlModelResult.getValidationResults());
    }

    private void assertErrorPath(List<ValidationResult> validationResults)
    {
        assertThat(validationResults.size(), is(2));

        assertThat(validationResults.get(0).getStartLine(), is(2));
        assertThat(validationResults.get(0).getStartColumn(), is(1));
        assertThat(validationResults.get(0).getEndLine(), is(2));
        assertThat(validationResults.get(0).getEndColumn(), is(15));

        assertThat(validationResults.get(1).getStartLine(), is(9));
        assertThat(validationResults.get(1).getStartColumn(), is(9));
        assertThat(validationResults.get(1).getEndLine(), is(9));
        assertThat(validationResults.get(1).getEndColumn(), is(21));

    }
}
