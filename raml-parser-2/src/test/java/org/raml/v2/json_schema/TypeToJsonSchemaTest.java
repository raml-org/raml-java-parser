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
package org.raml.v2.json_schema;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeToJsonSchemaVisitor;
import org.raml.yagi.framework.nodes.Node;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


@RunWith(Parameterized.class)
public class TypeToJsonSchemaTest extends TestDataProvider
{

    public TypeToJsonSchemaTest(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(TypeToJsonSchemaTest.class.getResource("").toURI(), "input.raml", "output.json");
    }

    @Test
    public void test() throws IOException, SAXException
    {
        final RamlBuilder builder = new RamlBuilder();
        final Node build = builder.build(input);
        final List<TypeDeclarationField> fields = build.findDescendantsWith(TypeDeclarationField.class);
        for (TypeDeclarationField field : fields)
        {
            if (field.getName().equals("root"))
            {
                final ResolvedType resolvedType = ((TypeDeclarationNode) field.getValue()).getResolvedType();
                JSONObject actual = new TypeToJsonSchemaVisitor().transform(resolvedType);
                dump = actual.toString();
                expected = IOUtils.toString(new FileInputStream(expectedOutput));
                assertTrue(jsonEquals(dump, expected));
                return;
            }
        }
        fail("No type called root was found");
    }

}
