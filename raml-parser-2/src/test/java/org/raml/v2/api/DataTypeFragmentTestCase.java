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
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.api.model.v10.api.Library;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DataTypeFragmentTestCase
{
    @Test
    public void checkThatTypeDeclarationRetrievedFromDataTypeFragmentImplementsMostSpecializedTypeDeclarationInterface() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/fragments/type-declaration/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        assertThat(ramlModelResult.getFragment(), is(RamlFragment.DataType));

        TypeDeclaration typeDeclaration = ramlModelResult.getTypeDeclaration();
        assertThat(typeDeclaration, is(instanceOf(ObjectTypeDeclaration.class)));
    }
}
