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
package org.raml.v2.internal.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.yagi.framework.nodes.Position;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class SchemaGeneratorTest extends TestDataProvider
{
    private static final String SCHEMA_FILE_NAME = "schema.json";
    private static final String EXAMPLE_FILE_NAME = "input.json";

    public SchemaGeneratorTest(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Test
    public void verifyJson() throws IOException, ProcessingException
    {
        String content = IOUtils.toString(input.toURI());
        ExternalSchemaTypeExpressionNode node = new ExternalSchemaTypeExpressionNode(content);
        Position position = Mockito.mock(Position.class);

        Mockito.when(position.getIncludedResourceUri()).thenReturn(input.toURI().toString());
        node.setStartPosition(position);
        node.setEndPosition(position);

        JsonSchemaExternalType jsonSchemaExternalType = (JsonSchemaExternalType) node.generateDefinition();
        JsonSchema jsonSchema = SchemaGenerator.generateJsonSchema(jsonSchemaExternalType);
        ProcessingReport report = jsonSchema.validate(new ObjectMapper().disableDefaultTyping().readTree(expectedOutput));

        assertTrue(report.isSuccess());
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(SchemaGeneratorTest.class.getResource("").toURI(), SCHEMA_FILE_NAME, EXAMPLE_FILE_NAME);
    }
}
