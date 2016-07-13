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
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.jackson.JNodeParser;
import org.raml.yagi.framework.phase.Phase;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.AnyResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeToRuleVisitor;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.impl.v10.type.TypeToSchemaVisitor;
import org.xml.sax.SAXException;

import javax.annotation.Nullable;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import static org.raml.v2.internal.utils.ValueUtils.defaultTo;

public class ExampleValidationPhase implements Phase
{
    private ResourceLoader resourceLoader;

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
            if (!exampleTypeNode.isStrict())
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
            if (isXmlValue(value))
            {
                return validateXml(type, resolvedType, value);
            }
            else if (isJsonValue(value))
            {
                return validateJson(exampleValue, resolvedType, value);
            }
            else
            {
                final Rule rule = resolvedType.visit(new TypeToRuleVisitor(resourceLoader));
                return rule.apply(exampleValue);
            }
        }
        else if (exampleValue != null)
        {
            final Rule rule = resolvedType.visit(new TypeToRuleVisitor(resourceLoader));
            return rule.apply(exampleValue);
        }
        else
        {
            return null;
        }

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
        final TypeToSchemaVisitor typeToSchemaVisitor = new TypeToSchemaVisitor();
        typeToSchemaVisitor.transform(defaultTo(type.getTypeName(), "raml-root"), resolvedType);
        final XmlSchema schema = typeToSchemaVisitor.getSchema();
        final StringWriter xsd = new StringWriter();
        schema.write(xsd);
        try
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            final Validator validator = factory.newSchema(new StreamSource(new StringReader(xsd.toString()))).newValidator();
            validator.validate(new StreamSource(new StringReader(value)));
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
}
