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
package org.raml.v2.internal.framework.grammar.rule;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.nodes.ExampleTypeNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYObjectNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.utils.SchemaGenerator;

public class JsonSchemaValidationRule extends Rule
{

    private JsonSchema schema;

    public JsonSchemaValidationRule(Node schemaNode)
    {
        try
        {
            this.schema = new SchemaGenerator().generateJsonSchema(schemaNode);
        }
        catch (Exception e)
        {
            this.schema = null;
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return false;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (schema == null)
        {
            return ErrorNodeFactory.createInvalidJsonExampleNode("Invalid JsonSchema");
        }
        try
        {
            Node source = node.getSource();
            if (source == null)
            {
                if (!(node instanceof StringNode) && !(node instanceof ObjectNode))
                {
                    return ErrorNodeFactory.createInvalidJsonExampleNode("Source was null");
                }
                else
                {
                    source = node;
                }
            }
            String value = null;

            if (source instanceof StringNode)
            {
                value = ((StringNode) source).getValue();
            }
            else if (source instanceof SYObjectNode)
            {
                value = ((ExampleTypeNode) node).toJsonString();
            }

            if (value == null)
            {
                return ErrorNodeFactory.createInvalidJsonExampleNode("Source example is not valid: " + source);
            }

            JsonNode json = JsonLoader.fromString(value);
            ProcessingReport report = schema.validate(json);
            if (!report.isSuccess())
            {
                Iterator<ProcessingMessage> iterator = report.iterator();
                List<String> errors = Lists.newArrayList();
                while (iterator.hasNext())
                {
                    ProcessingMessage next = iterator.next();
                    errors.add(next.getMessage());
                }
                node.replaceWith(ErrorNodeFactory.createInvalidJsonExampleNode("{\n" + Joiner.on(",\n").join(errors) + "\n}"));
            }
        }
        catch (IOException | ProcessingException e)
        {
            node.replaceWith(ErrorNodeFactory.createInvalidNode(node));
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        return "JSON Schema validation rule";
    }
}
