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
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.utils.xml.XsdResourceResolver;
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
        String includedResourceUri = resolveResourceUriIfIncluded(jsonTypeDefinition);

        JsonNode jsonSchema = JsonLoader.fromString(jsonTypeDefinition.getSchemaValue());
        JsonSchemaFactory factory = JsonSchemaFactory.newBuilder().freeze();
        if (jsonTypeDefinition.getInternalFragment() != null)
        {
            return factory.getJsonSchema(jsonSchema, "/definitions/" + jsonTypeDefinition.getInternalFragment());
        }
        else
        {
            if (includedResourceUri != null)
            {
                return factory.getJsonSchema(includedResourceUri);
            }
            else
            {
                return factory.getJsonSchema(jsonSchema);
            }

        }
    }

    private static String resolveResourceUriIfIncluded(JsonSchemaExternalType jsonTypeDefinition)
    {
        // Getting the type holding the schema
        TypeExpressionNode typeDeclarationNode = jsonTypeDefinition.getTypeExpressionNode();

        if (typeDeclarationNode instanceof ExternalSchemaTypeExpressionNode)
        {
            ExternalSchemaTypeExpressionNode schema = (ExternalSchemaTypeExpressionNode) typeDeclarationNode;

            return schema.getStartPosition().getIncludedResourceUri();
        }
        else
        {
            // Inside the type declaration, we find the node containing the schema itself
            List<ExternalSchemaTypeExpressionNode> schemas = typeDeclarationNode.findDescendantsWith(ExternalSchemaTypeExpressionNode.class);
            if (schemas.size() > 0)
            {
                return schemas.get(0).getStartPosition().getIncludedResourceUri();
            }
            else
            {
                // If the array is empty, then it must be a reference to a previously defined type
                List<NamedTypeExpressionNode> refNode = typeDeclarationNode.findDescendantsWith(NamedTypeExpressionNode.class);

                if (refNode.size() > 0)
                {
                    // If refNodes is not empty, then we obtain that type
                    typeDeclarationNode = refNode.get(0).resolveReference();
                    if (typeDeclarationNode != null)
                    {
                        schemas = typeDeclarationNode.findDescendantsWith(ExternalSchemaTypeExpressionNode.class);
                        if (schemas.size() > 0)
                        {
                            return schemas.get(0).getStartPosition().getIncludedResourceUri();
                        }
                    }
                }
            }
        }

        return null;
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
