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


import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.api.model.v10.api.Api;
import org.raml.v2.api.model.v10.datamodel.XMLTypeDeclaration;

import java.io.File;
import java.io.IOException;


public class XmlExternalFileTestCase
{
    @Test
    public void testSchemaPath() throws IOException
    {
        File input = new File("src/test/resources/org/raml/v2/api/v10/xml-external/input.raml");
        Assert.assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        Assert.assertFalse(ramlModelResult.hasErrors());

        Api apiV10 = ramlModelResult.getApiV10();

        Assert.assertTrue(((XMLTypeDeclaration) apiV10.resources().get(0).methods().get(0).body().get(0)).schemaPath().endsWith("person-schema.xsd"));
    }
}
