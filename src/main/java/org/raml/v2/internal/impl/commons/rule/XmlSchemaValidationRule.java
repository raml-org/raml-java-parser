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

import com.google.common.collect.Lists;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeDefinition;
import org.raml.v2.internal.utils.SchemaGenerator;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Validates a string node content with the specified xml schema
 */
public class XmlSchemaValidationRule extends Rule
{

    private Schema schema;
    private String type;

    public XmlSchemaValidationRule(XmlSchemaTypeDefinition schemaNode, ResourceLoader resourceLoader)
    {
        try
        {
            this.schema = SchemaGenerator.generateXmlSchema(resourceLoader, schemaNode);
            this.type = schemaNode.getInternalFragment();
        }
        catch (SAXException e)
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
        return node instanceof StringNode;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (schema == null)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode("Invalid XmlSchema");
        }
        if (node instanceof SimpleTypeNode)
        {
            return validateXmlExample(node);
        }
        // else We only validate xml schema against xml examples so we do nothing
        return node;
    }

    private Node validateXmlExample(@Nonnull Node node)
    {
        String value = ((SimpleTypeNode) node).getLiteralValue();
        try
        {
            if (this.type != null && !value.trim().startsWith("<" + this.type))
            {
                return ErrorNodeFactory.createInvalidXmlExampleNode("Provided object is not of type " + this.type);
            }
            else
            {
                schema.newValidator().validate(new StreamSource(new StringReader(value)));
            }
        }
        catch (SAXException | IOException e)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage());
        }
        return node;
    }

    @Override
    public String getDescription()
    {
        return "Xml Schema validation Rule.";
    }
}
