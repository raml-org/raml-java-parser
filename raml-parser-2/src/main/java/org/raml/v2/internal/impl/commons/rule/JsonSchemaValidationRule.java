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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
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
                return ErrorNodeFactory.createInvalidJsonExampleNode("{\n" + Joiner.on(",\n").join(errors) + "\n}");
            }
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
}
