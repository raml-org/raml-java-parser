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
package org.raml.v2.internal.impl.v10.phase;

import org.apache.ws.commons.schema.XmlSchema;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.model.factory.TypeDeclarationModelFactory;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.impl.v10.type.*;
import org.raml.v2.internal.utils.xml.XMLLocalConstants;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.NullValueRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.*;
import org.raml.yagi.framework.nodes.jackson.JNodeParser;
import org.raml.yagi.framework.nodes.snakeyaml.NodeParser;
import org.raml.yagi.framework.phase.Phase;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.annotation.Nullable;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.raml.v2.internal.utils.xml.XMLLocalConstants.DISALLOW_DOCTYPE_DECL_FEATURE;
import static org.raml.v2.internal.utils.xml.XMLLocalConstants.EXTERNAL_GENERAL_ENTITIES_FEATURE;
import static org.raml.v2.internal.utils.xml.XMLLocalConstants.EXTERNAL_PARAMETER_ENTITIES_FEATURE;
import static org.raml.v2.internal.utils.xml.XMLLocalConstants.expandEntities;
import static org.raml.v2.internal.utils.xml.XMLLocalConstants.externalEntities;

public class ExampleValidationPhase implements Phase
{
    private ResourceLoader resourceLoader;
    private static final String ERROR_MESSAGE_MAX_LENGTH = "raml.error_message_max_length";
    public static int errorMessageMaxLength = Integer.parseInt(System.getProperty(ERROR_MESSAGE_MAX_LENGTH, "10000"));

    private static final String NILLABLE_TYPES_PROP = "org.raml.nillable_types";
    public static boolean NILLABLE_TYPES = Boolean.parseBoolean(System.getProperty(NILLABLE_TYPES_PROP, "false"));


    public ExampleValidationPhase(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public Node apply(Node tree)
    {
        final List<ExampleDeclarationNode> descendantsWith = tree.findDescendantsWith(ExampleDeclarationNode.class);
        for (ExampleDeclarationNode exampleTypeNode : descendantsWith)
        {
            if (exampleTypeNode.isStrict())
            {
                final TypeDeclarationNode type = org.raml.yagi.framework.util.NodeUtils.getAncestor(exampleTypeNode, TypeDeclarationNode.class);
                final Node exampleValue = exampleTypeNode.getExampleValue();
                if (type != null)
                {
                    final Node validate = validate(type, exampleValue);
                    if (validate != null)
                    {
                        exampleValue.replaceWith(validate);
                    }
                }
            }
        }
        return tree;
    }

    @Nullable
    public Node validate(TypeDeclarationNode type, String exampleValue)
    {
        Node exampleValueNode = new StringNodeImpl(exampleValue);

        if (exampleValue == null || (isBlank(exampleValue) && !(type.getResolvedType() instanceof StringResolvedType)))
        {
            exampleValueNode = new NullNodeImpl();
        }
        else if (!(type.getResolvedType() instanceof StringResolvedType) &&
                 !(type.getResolvedType() instanceof FileResolvedType) &&
                 !isJsonValue(exampleValue) &&
                 !isXmlValue(exampleValue))
        {
            // parse as yaml except for string, file, json and xml types
            exampleValueNode = NodeParser.parse(resourceLoader, "", exampleValue);
        }
        return validate(type, exampleValueNode);
    }

    @Nullable
    public Node validate(TypeDeclarationNode type, Node exampleValue)
    {
        final ResolvedType resolvedType = type.getResolvedType();
        if (resolvedType instanceof AnyResolvedType) // If accepts any no need for validation
        {
            return null;
        }
        if (exampleValue instanceof StringNode && !isExternalSchemaType(resolvedType))
        {
            final String value = ((StringNode) exampleValue).getValue();
            if ((mightBeAnObjectType(resolvedType)) && isXmlValue(value))
            {
                return validateXml(type, resolvedType, value);
            }
            else if ((mightBeAnObjectType(resolvedType) || (resolvedType instanceof ArrayResolvedType)) && isJsonValue(value))
            {
                return validateJson(exampleValue, resolvedType, value);
            }
        }
        if (exampleValue != null)
        {
            final Rule rule = visitAppropriately(resolvedType);
            return rule != null ? rule.apply(exampleValue) : null;
        }
        return null;
    }

    private Rule visitAppropriately(ResolvedType resolvedType)
    {
        if (NILLABLE_TYPES)
        {
            return new AnyOfRule(resolvedType.visit(new TypeToRuleVisitor(resourceLoader, false)), new NullValueRule());
        }
        else
        {

            return resolvedType.visit(new TypeToRuleVisitor(resourceLoader, false));
        }
    }

    private boolean mightBeAnObjectType(ResolvedType resolvedType)
    {
        if (resolvedType instanceof ObjectResolvedType)
        {
            return true;
        }

        return unionMightBeAnObject(resolvedType, new HashSet<String>());
    }

    private boolean unionMightBeAnObject(ResolvedType resolvedType, HashSet<String> seenTypes)
    {

        seenTypes.add(resolvedType.getTypeName());
        if (resolvedType instanceof UnionResolvedType)
        {
            UnionResolvedType urt = (UnionResolvedType) resolvedType;
            for (ResolvedType type : urt.of())
            {
                if (!seenTypes.contains(type.getTypeName()))
                {
                    return unionMightBeAnObject(type, seenTypes);
                }
            }
        }

        // return resolvedType instanceof ObjectResolvedType || (resolvedType instanceof ArrayResolvedType && unionMightBeAnObject(((ArrayResolvedType) resolvedType).getItems(), new
        // HashSet<String>()));

        return resolvedType instanceof ObjectResolvedType || (resolvedType instanceof ArrayResolvedType);
    }

    protected Node validateJson(Node exampleValue, ResolvedType resolvedType, String value)
    {
        final Rule rule = resolvedType.visit(new TypeToRuleVisitor(resourceLoader));
        final Node parse = JNodeParser.parse(resourceLoader, "", value);

        if (parse.getType() != NodeType.Error)
        {
            final Node apply = rule.apply(parse);
            final List<ErrorNode> errorNodeList = apply.findDescendantsWith(ErrorNode.class);

            if (apply instanceof ErrorNode)
            {
                errorNodeList.add(0, (ErrorNode) apply);
            }

            if (!errorNodeList.isEmpty())
            {
                String errorMessage = "";
                for (ErrorNode errorNode : errorNodeList)
                {
                    if (errorMessage.length() > errorMessageMaxLength)
                        break;
                    if (errorMessage.isEmpty())
                    {
                        errorMessage = "- " + errorNode.getErrorMessage();
                    }
                    else
                    {
                        errorMessage += "\n" + "- " + errorNode.getErrorMessage();
                    }
                }
                return ErrorNodeFactory.createInvalidJsonExampleNode(errorMessage);
            }
            else
            {
                return exampleValue;
            }
        }
        else
        {
            return parse;
        }
    }

    @Nullable
    protected Node validateXml(TypeDeclarationNode type, ResolvedType resolvedType, String value)
    {
        final TypeToXmlSchemaVisitor typeToXmlSchemaVisitor = new TypeToXmlSchemaVisitor();
        typeToXmlSchemaVisitor.transform(new TypeDeclarationModelFactory().create(type).rootElementName(), resolvedType);
        final XmlSchema schema = typeToXmlSchemaVisitor.getSchema();
        final StringWriter xsd = new StringWriter();
        schema.write(xsd);
        try
        {
            final SchemaFactory factory = SchemaFactory.newInstance(XMLLocalConstants.XML_SCHEMA_VERSION);
            final Schema schema1 = factory.newSchema(new StreamSource(new StringReader(xsd.toString())));
            final Validator validator = schema1.newValidator();

            final XMLReader xmlReader = XMLReaderFactory.createXMLReader();

            xmlReader.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, !expandEntities);
            xmlReader.setFeature(EXTERNAL_GENERAL_ENTITIES_FEATURE, externalEntities);
            xmlReader.setFeature(EXTERNAL_PARAMETER_ENTITIES_FEATURE, externalEntities);

            validator.validate(new SAXSource(
                    new NamespaceFilter(xmlReader, TypeToXmlSchemaVisitor.getTargetNamespace(resolvedType)),
                    new InputSource(new StringReader(value))));
        }
        catch (IOException | SAXException e)
        {
            return ErrorNodeFactory.createInvalidXmlExampleNode(e.getMessage());
        }
        return null;
    }

    private boolean isXmlValue(String value)
    {
        return value.trim().startsWith("<");
    }

    private boolean isJsonValue(String value)
    {
        return value.trim().startsWith("{") || value.trim().startsWith("[");
    }

    private boolean isExternalSchemaType(ResolvedType resolvedType)
    {
        return resolvedType instanceof XmlSchemaExternalType || resolvedType instanceof JsonSchemaExternalType;
    }


    private static class NamespaceFilter extends XMLFilterImpl
    {

        String requiredNamespace;

        public NamespaceFilter(XMLReader parent, String requiredNamespace)
        {
            super(parent);
            this.requiredNamespace = requiredNamespace;
        }

        @Override
        public void startElement(String arg0, String arg1, String arg2, Attributes arg3) throws SAXException
        {
            if (!arg0.equals(requiredNamespace))
                arg0 = requiredNamespace;
            super.startElement(arg0, arg1, arg2, arg3);
        }
    }
}
