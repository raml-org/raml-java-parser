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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.raml.v2.internal.utils.xml.XsdResourceResolver;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.xml.sax.SAXException;

public class SchemaGenerator
{

    public static Schema generateXmlSchema(ResourceLoader resourceLoader, XmlSchemaExternalType schemaNode) throws SAXException
    {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(new XsdResourceResolver(resourceLoader, schemaNode.getSchemaPath()));
        return factory.newSchema(new StreamSource(new StringReader(schemaNode.getSchemaValue())));
    }

    public static JsonSchema generateJsonSchema(JsonSchemaExternalType jsonTypeDefinition) throws IOException, ProcessingException
    {
        JsonNode jsonSchema = JsonLoader.fromString(jsonTypeDefinition.getSchemaValue());
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().freeze();
        if (jsonTypeDefinition.getInternalFragment() != null)
        {
            return factory.getJsonSchema(jsonSchema, "/definitions/" + jsonTypeDefinition.getInternalFragment());
        }
        else
        {
            return factory.getJsonSchema(jsonSchema);
        }
    }


    public static boolean isJsonSchema(String schema)
    {
        return schema.trim().startsWith("{");
    }


    public static boolean isXmlSchema(String schema)
    {
        return schema.trim().startsWith("<");
    }

}
