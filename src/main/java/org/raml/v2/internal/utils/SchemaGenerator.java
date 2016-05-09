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

import static org.raml.v2.internal.utils.NodeUtils.isStringNode;

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

import org.raml.v2.internal.framework.grammar.rule.xml.XsdResourceResolver;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SchemaNodeImpl;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.xml.sax.SAXException;

public class SchemaGenerator
{

    private ResourceLoader resourceLoader;

    public SchemaGenerator(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    public SchemaGenerator()
    {
    }

    public Schema generateXmlSchema(Node node) throws SAXException
    {
        if (!isXmlSchemaNode(node))
        {
            throw new SAXException("invalid xml schema");
        }
        SchemaNodeImpl schema = (SchemaNodeImpl) node;
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(new XsdResourceResolver(resourceLoader, schema.getSchemaPath()));
        return factory.newSchema(new StreamSource(new StringReader(schema.getValue())));
    }

    public JsonSchema generateJsonSchema(Node node) throws IOException, ProcessingException
    {
        if (!isJsonSchemaNode(node))
        {
            throw new ProcessingException("invalid json schema");
        }
        SchemaNodeImpl schema = (SchemaNodeImpl) node;
        JsonNode jsonSchema = JsonLoader.fromString(schema.getValue());
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().freeze();
        if (schema.getTypeReference() != null)
        {
            return factory.getJsonSchema(jsonSchema, "/definitions/" + schema.getTypeReference());
        }
        else
        {
            return factory.getJsonSchema(jsonSchema);
        }
    }

    public static boolean isJsonSchemaNode(Node node)
    {
        return isStringNode(node) && nodeStartsWith((StringNode) node, "{");
    }

    public static boolean isXmlSchemaNode(Node node)
    {
        return isStringNode(node) && nodeStartsWith((StringNode) node, "<");
    }

    private static boolean isSchema(Node node)
    {
        return node instanceof SchemaNodeImpl;
    }

    public static boolean isSchemaNode(final Node node)
    {
        return isSchema(node) || isJsonSchemaNode(node) || isXmlSchemaNode(node);
    }

    public static boolean nodeStartsWith(StringNode node, String prefix)
    {
        return node.getValue().startsWith(prefix);
    }

    public static void wrapNode(Node node, String actualPath)
    {
        SchemaNodeImpl schemaNode = new SchemaNodeImpl((StringNode) node, actualPath);
        node.replaceWith(schemaNode);
    }
}
