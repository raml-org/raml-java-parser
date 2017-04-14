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

public class ValidationResultsTestCase
{
    @Test
    public void errorPathTest() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/error-path/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertTrue(ramlModelResult.hasErrors());

        assertErrorPath(ramlModelResult.getValidationResults());
    }

    private void assertErrorPath(List<ValidationResult> validationResults)
    {
        assertThat(validationResults.size(), is(11));

        assertThat(validationResults.get(0).getPath(), is("/types/Matrix/example/0/1"));
        assertThat(validationResults.get(1).getPath(), is("/types/Matrix/example/1/1"));
        assertThat(validationResults.get(2).getPath(), is("/types/User/examples/badExample/family/1"));
        assertThat(validationResults.get(3).getPath(), is("/types/User/examples/anotherBadExample"));
        assertThat(validationResults.get(4).getPath(), is("/types/User/examples/anotherBadExample"));
        assertThat(validationResults.get(5).getPath(), is("/types/User/examples/anotherOne/name"));
        assertThat(validationResults.get(6).getPath(), is("/types/User/examples/anotherOne/age"));
        assertThat(validationResults.get(7).getPath(), is("/types/Book/examples/one/users/1/family/1"));
        assertThat(validationResults.get(8).getPath(), is("/types/Book/examples/one/users/1/family/2"));
        assertThat(validationResults.get(9).getPath(), is("/types/Book/examples/one/users/2/age"));
        assertThat(validationResults.get(10).getPath(), is("/types/Book/examples/two/authors/1"));

    }
}
