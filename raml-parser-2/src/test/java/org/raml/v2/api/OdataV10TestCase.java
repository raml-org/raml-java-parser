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
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.IntegerTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class OdataV10TestCase
{

    @Test
    public void testApi() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/odata/input.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        assertFalse(ramlModelResult.hasErrors());
        Api api = ramlModelResult.getApiV10();

        final TypeDeclaration typeDeclaration = api.types().get(0);
        assertThat(typeDeclaration, instanceOf(ObjectTypeDeclaration.class));
        final ObjectTypeDeclaration orderType = (ObjectTypeDeclaration) typeDeclaration;
        assertThat(orderType.name(), is("order"));
        final List<TypeDeclaration> properties = orderType.properties();
        assertThat(properties.size(), is(3));
        final TypeDeclaration orderId = properties.get(0);
        assertThat(orderId, instanceOf(IntegerTypeDeclaration.class));
        IntegerTypeDeclaration orderIdField = (IntegerTypeDeclaration) orderId;
        assertThat(orderIdField.annotations().size(), is(2));
    }


}
