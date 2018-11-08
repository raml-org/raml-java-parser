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
import org.raml.v2.api.model.v10.datamodel.ArrayTypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.datamodel.UnionTypeDeclaration;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class TypeDeclarationValidationTestCase
{

    @Test
    public void unionAndArrayValidation() {
        File input = new File("src/test/resources/org/raml/v2/api/v10/validation/union-array-validation.raml");
        assertTrue(input.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);

        List<TypeDeclaration> typeDeclarations = ramlModelResult.getApiV10().resources().get(0).methods().get(0).queryParameters();
        assertTrue(((UnionTypeDeclaration) typeDeclarations.get(0)).of().get(1).validate("100").isEmpty());
        assertTrue(((ArrayTypeDeclaration) typeDeclarations.get(1)).items().validate("20").isEmpty());
    }
}
