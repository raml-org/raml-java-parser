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
package org.raml.v2.internal.impl.commons.phase;

import com.fasterxml.jackson.core.JsonParseException;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.phase.Transformer;
import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeDefinition;
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeDefinition;
import org.raml.v2.internal.utils.SchemaGenerator;

/**
 * Validates that the external schemas are valid schemas.
 */
public class SchemaValidationTransformer implements Transformer
{


    private ResourceLoader resourceLoader;

    public SchemaValidationTransformer(ResourceLoader resourceLoader)
    {

        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ExternalSchemaTypeExpressionNode;
    }

    @Override
    public Node transform(Node node)
    {
        ExternalSchemaTypeExpressionNode schema = (ExternalSchemaTypeExpressionNode) node;
        try
        {
            final TypeDefinition typeDefinition = schema.generateDefinition();
            if (typeDefinition instanceof XmlSchemaTypeDefinition)
            {
                SchemaGenerator.generateXmlSchema(resourceLoader, (XmlSchemaTypeDefinition) typeDefinition);
            }
            else if (typeDefinition instanceof JsonSchemaTypeDefinition)
            {
                SchemaGenerator.generateJsonSchema((JsonSchemaTypeDefinition) typeDefinition);
            }
        }
        catch (JsonParseException ex)
        {
            return ErrorNodeFactory.createInvalidSchemaNode(ex.getOriginalMessage());
        }
        catch (Exception e)
        {
            return ErrorNodeFactory.createInvalidSchemaNode(e.getMessage());
        }
        return node;
    }
}
