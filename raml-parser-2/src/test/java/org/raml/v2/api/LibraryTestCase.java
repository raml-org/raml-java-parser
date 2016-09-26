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


import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.RamlFragment;

public class LibraryTestCase
{

    @Test
    public void library() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/library/assets-lib.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        assertThat(ramlModelResult.getFragment(), is(RamlFragment.Library));

        Library library = ramlModelResult.getLibrary();
        assertThat(library.types(), hasSize(4));
        assertThat(library.traits(), hasSize(1));
    }

    @Test
    public void cyclicLibraryDependency() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/cyclic-uses/file1.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertTrue(ramlModelResult.hasErrors());
        assertTrue(ramlModelResult.getValidationResults().size() == 1);
        assertEquals("Cyclic dependency loading file: src/test/resources/org/raml/v2/api/v10/cyclic-uses/file1.raml", ramlModelResult.getValidationResults().get(0).getMessage());
    }

    @Test
    public void selfCyclic() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/cyclic-uses/file3.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertTrue(ramlModelResult.hasErrors());
        assertTrue(ramlModelResult.getValidationResults().size() == 1);
        assertEquals("Cyclic dependency loading file: src/test/resources/org/raml/v2/api/v10/cyclic-uses/file3.raml", ramlModelResult.getValidationResults().get(0).getMessage());
    }

}
