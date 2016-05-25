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

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.RamlNodeParser;
import org.raml.v2.internal.framework.phase.Phase;
import org.raml.v2.internal.impl.commons.nodes.ExampleDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeDefinition;
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.type.TypeToRuleVisitor;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeDefinition;
import org.raml.v2.internal.utils.NodeUtils;

import java.util.List;

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
                final TypeDeclarationNode type = NodeUtils.getAncestor(exampleTypeNode, TypeDeclarationNode.class);
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

    public Node validate(TypeDeclarationNode type, Node exampleValue)
    {
        final TypeDefinition typeDefinition = type.getTypeDefinition();
        final Rule rule = typeDefinition.visit(new TypeToRuleVisitor(resourceLoader));

        if (exampleValue instanceof StringNode && !isExternalSchemaType(typeDefinition))
        {
            final String value = ((StringNode) exampleValue).getValue();
            if (isXmlValue(value))
            {
                // TODO add xml validation based on type definition
            }
            else if (isJsonValue(value))
            {
                final Node parse = RamlNodeParser.parse("", value);
                final Node apply = rule.apply(parse);
                final List<ErrorNode> errorNodeList = apply.findDescendantsWith(ErrorNode.class);
                if (apply instanceof ErrorNode || !errorNodeList.isEmpty())
                {
                    String errorMessage = "";
                    if (apply instanceof ErrorNode)
                    {
                        errorMessage += "- " + ((ErrorNode) apply).getErrorMessage();
                    }
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
                return rule.apply(exampleValue);
            }
        }
        else if (exampleValue != null)
        {
            return rule.apply(exampleValue);
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

    private boolean isExternalSchemaType(TypeDefinition typeDefinition)
    {
        return typeDefinition instanceof XmlSchemaTypeDefinition || typeDefinition instanceof JsonSchemaTypeDefinition;
    }
}
