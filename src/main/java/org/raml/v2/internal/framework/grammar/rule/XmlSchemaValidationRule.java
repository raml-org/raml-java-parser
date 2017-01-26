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

import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.SchemaNodeImpl;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.utils.SchemaGenerator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XmlSchemaValidationRule extends Rule
{
    public static final String EXTERNAL_ENTITIES_PROPERTY = "raml.xml.expandExternalEntities";
    public static final String EXPAND_ENTITIES_PROPERTY = "raml.xml.expandInternalEntities";

    private static final Boolean externalEntities =
            Boolean.parseBoolean(System.getProperty(EXTERNAL_ENTITIES_PROPERTY, "false"));
    private static final Boolean expandEntities =
            Boolean.parseBoolean(System.getProperty(EXPAND_ENTITIES_PROPERTY, "false"));

    private Schema schema;
    private String type;

    public XmlSchemaValidationRule(Node schemaNode, ResourceLoader resourceLoader)
    {
        try
        {
            this.schema = new SchemaGenerator(resourceLoader).generateXmlSchema(schemaNode);
            this.type = ((SchemaNodeImpl) schemaNode).getTypeReference();
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
        return false;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (schema == null)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode("Invalid XmlSchema");
        }
        Node source = node.getSource();
        if (source == null)
        {
            if (node instanceof StringNode)
            {
                source = node;
            }
            else if (!(node instanceof ObjectNode))
            {
                return ErrorNodeFactory.createInvalidXmlExampleNode("Source was null");
            }
            else
            {
                if (node.getChildren().size() == 1 &&
                    node.getChildren().get(0) instanceof KeyValueNode &&
                    (((KeyValueNode) node.getChildren().get(0)).getValue()) instanceof StringNode)
                {
                    source = ((KeyValueNode) node.getChildren().get(0)).getValue();
                }
            }
        }
        if (source instanceof StringNode)
        {
            internalValidateExample(node, (StringNode) source);
        }
        return node;
    }

    private void internalValidateExample(@Nonnull Node node, StringNode source)
    {
        String value = source.getValue();
        try
        {
            if (this.type != null && !value.startsWith("<" + this.type))
            {
                node.replaceWith(ErrorNodeFactory.createInvalidXmlExampleNode("provided object is not of type " + this.type));
            }
            else
            {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                setFeatures(factory);

                DocumentBuilder builder = factory.newDocumentBuilder();
                builder.setErrorHandler(null);
                Document document = builder.parse(new InputSource(new StringReader(value)));

                schema.newValidator().validate(new DOMSource(document.getDocumentElement()));
            }
        }
        catch (SAXException | IOException | ParserConfigurationException e)
        {
            node.replaceWith(ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage()));
        }
    }

    private void setFeatures(DocumentBuilderFactory dbf) throws ParserConfigurationException
    {
        String feature;

        // If you can't completely disable DTDs, then at least do the following:
        feature = "http://xml.org/sax/features/external-general-entities";
        dbf.setFeature(feature, externalEntities);

        feature = "http://xml.org/sax/features/external-parameter-entities";
        dbf.setFeature(feature, externalEntities);

        feature = "http://apache.org/xml/features/disallow-doctype-decl";
        dbf.setFeature(feature, !expandEntities);

        // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks" (see reference below)
        dbf.setXIncludeAware(expandEntities);
        dbf.setExpandEntityReferences(expandEntities);
        dbf.setNamespaceAware(true);
    }

    @Override
    public String getDescription()
    {
        return null;
    }
}
