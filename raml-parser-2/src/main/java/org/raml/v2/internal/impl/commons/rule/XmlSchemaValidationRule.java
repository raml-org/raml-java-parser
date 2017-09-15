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
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.utils.SchemaGenerator;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Validates a string node content with the specified xml schema
 */
public class XmlSchemaValidationRule extends Rule
{
    public static final String EXTERNAL_ENTITIES_PROPERTY = "raml.xml.expandExternalEntities";
    public static final String EXPAND_ENTITIES_PROPERTY = "raml.xml.expandInternalEntities";

    public static final Boolean externalEntities =
            Boolean.parseBoolean(System.getProperty(EXTERNAL_ENTITIES_PROPERTY, "false"));
    public static final Boolean expandEntities =
            Boolean.parseBoolean(System.getProperty(EXPAND_ENTITIES_PROPERTY, "false"));
    public static final String EXTERNAL_GENERAL_ENTITIES_FEATURE = "http://xml.org/sax/features/external-general-entities";
    public static final String EXTERNAL_PARAMETER_ENTITIES_FEATURE = "http://xml.org/sax/features/external-parameter-entities";
    public static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

    private Schema schema;
    private String type;

    public XmlSchemaValidationRule(XmlSchemaExternalType schemaNode, ResourceLoader resourceLoader)
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
            if (this.type != null)
            {
                final QName rootElement = getRootElement(value);
                if (rootElement != null && !rootElement.getLocalPart().equals(type))
                {
                    return ErrorNodeFactory.createInvalidXmlExampleNode("Provided object is not of type " + this.type);
                }
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            setFeatures(factory);

            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(null);
            Document document = builder.parse(new InputSource(new StringReader(value)));

            schema.newValidator().validate(new DOMSource(document.getDocumentElement()));
        }
        catch (XMLStreamException | SAXException | IOException | ParserConfigurationException e)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage());
        }
        return node;
    }

    private void setFeatures(DocumentBuilderFactory dbf) throws ParserConfigurationException
    {
        String feature = null;

        // If you can't completely disable DTDs, then at least do the following:
        dbf.setFeature(EXTERNAL_GENERAL_ENTITIES_FEATURE, externalEntities);

        dbf.setFeature(EXTERNAL_PARAMETER_ENTITIES_FEATURE, externalEntities);

        dbf.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, !expandEntities);

        // and these as well, per Timothy Morgan's 2014 paper: "XML Schema, DTD, and Entity Attacks" (see reference below)
        dbf.setXIncludeAware(expandEntities);
        dbf.setExpandEntityReferences(expandEntities);
        dbf.setNamespaceAware(true);
    }

    @Nullable
    public QName getRootElement(String xmlContent) throws XMLStreamException
    {
        XMLInputFactory f = XMLInputFactory.newInstance();
        XMLStreamReader r = f.createXMLStreamReader(new StringReader(xmlContent));
        r.nextTag();
        return r.getName();
    }

    @Override
    public String getDescription()
    {
        return "Xml Schema validation Rule.";
    }
}
