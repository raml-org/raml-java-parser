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
package org.raml.v2.internal.impl.commons.rule;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.utils.JSonDumper;
import org.raml.v2.internal.utils.SchemaGenerator;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.raml.yagi.framework.nodes.jackson.JsonUtils.parseJson;
import static java.io.File.separator;

/**
 * Validates a string node content with the specified json schema
 */
public class JsonSchemaValidationRule extends Rule
{

    private JsonSchema schema;

    public JsonSchemaValidationRule(JsonSchemaExternalType schemaNode)
    {
        try
        {
            this.schema = SchemaGenerator.generateJsonSchema(schemaNode);
        }
        catch (Exception e)
        {
            this.schema = null;
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Lists.newArrayList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode;
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
            String value;
            if (node instanceof StringNode)
            {
                value = ((StringNode) node).getValue();
            }
            else
            {
                value = JSonDumper.dump(node);
            }

            if (value == null)
            {
                return ErrorNodeFactory.createInvalidJsonExampleNode("Source example is not valid: " + node);
            }

            JsonNode json = parseJson(value);

            ProcessingReport report = schema.validate(json);
            if (!report.isSuccess())
            {
                Iterator<ProcessingMessage> iterator = report.iterator();
                List<String> errors = Lists.newArrayList();
                while (iterator.hasNext())
                {
                    ProcessingMessage next = iterator.next();
                    errors.add(processErrorMessage(next.toString()));
                }
                return ErrorNodeFactory.createInvalidJsonExampleNode("{\n" + Joiner.on(",\n").join(errors) + "\n}");
            }
        }
        catch (JsonParseException e)
        {
            return ErrorNodeFactory.createInvalidJsonExampleNode(e.getOriginalMessage());
        }
        catch (IOException | ProcessingException e)
        {
            return ErrorNodeFactory.createInvalidJsonExampleNode("Invalid json content : " + node.toString());
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        return "JSON Schema validation rule";
    }

    private String processErrorMessage(String processingMessage)
    {
        final String SCHEMA_TEXT = "schema: {\"loadingURI\":\"";
        final String POINTER_TEXT = "\"pointer\":\"";

        int startOfSchema = processingMessage.indexOf(SCHEMA_TEXT);

        // If it is true then loadingUri is present in the message and we have to modify it
        if (startOfSchema >= 0)
        {
            int startOfPointer = processingMessage.indexOf(POINTER_TEXT);
            String completeUri = processingMessage.substring(startOfSchema + SCHEMA_TEXT.length(), startOfPointer - 2);

            // Finding the filename: It begins after the last '/' in the uri.
            // If there is no '/', we display the entire processingMessage
            String reducedSchemaURI = completeUri.substring(completeUri.lastIndexOf(separator) + 1);

            // Replacing the complete URI with the reduced one
            return processingMessage.replace(completeUri, reducedSchemaURI);
        }

        return processingMessage;
    }
}
