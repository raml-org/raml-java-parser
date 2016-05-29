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
package org.raml.v2.internal.impl.commons.model;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.commons.model.builder.ModelUtils;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.utils.NodeSelector;
import org.raml.v2.internal.utils.NodeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public class TypeDeclaration extends Annotable
{

    private KeyValueNode node;

    public TypeDeclaration(KeyValueNode node)
    {
        this.node = node;
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }

    public String name()
    {
        return ((StringNode) node.getKey()).getValue();
    }

    public String schemaContent()
    {
        if (node.getValue() instanceof TypeDeclarationNode)
        {
            final List<TypeExpressionNode> baseTypes = ((TypeDeclarationNode) node.getValue()).getBaseTypes();
            if (!baseTypes.isEmpty())
            {
                final ResolvedType resolvedType = baseTypes.get(0).generateDefinition();
                if (resolvedType instanceof SchemaBasedResolvedType)
                {
                    return ((SchemaBasedResolvedType) resolvedType).getSchemaValue();
                }

            }
        }
        return null;
    }

    public List<String> schema()
    {
        return selectStringList("schema");
    }

    protected List<String> selectStringList(String propertyName)
    {
        final Node schema = NodeSelector.selectFrom(propertyName, node.getValue());
        if (schema instanceof ArrayNode)
        {
            final List<Node> children = schema.getChildren();
            final List<String> result = new ArrayList<>();
            for (Node child : children)
            {
                final Node rootSource = NodeUtils.getRootSource(child);
                if (rootSource instanceof SimpleTypeNode)
                {
                    result.add(((SimpleTypeNode) rootSource).getLiteralValue());
                }
            }
            return result;
        }
        else if (schema != null)
        {
            final Node rootSource = NodeUtils.getRootSource(schema);
            if (rootSource instanceof SimpleTypeNode)
            {
                return singletonList(((SimpleTypeNode) rootSource).getLiteralValue());
            }
        }
        return Collections.emptyList();
    }

    public List<String> type()
    {
        return selectStringList("type");
    }

    public List<RamlValidationResult> validate(String payload)
    {
        final ResourceLoader resourceLoader = new DefaultResourceLoader();
        final TypeDeclarationNode node = (TypeDeclarationNode) getNode();
        final ExampleValidationPhase exampleValidationPhase = new ExampleValidationPhase(resourceLoader);
        final Node validate = exampleValidationPhase.validate(node, new StringNodeImpl(payload));
        if (validate instanceof ErrorNode)
        {
            return singletonList(new RamlValidationResult((ErrorNode) validate));
        }
        else
        {
            return Collections.emptyList();
        }
    }

    public Boolean required()
    {
        Boolean required = ModelUtils.getSimpleValue("required", getNode());
        return required == null ? true : required;
    }

    public String defaultValue()
    {
        Object defaultValue = ModelUtils.getSimpleValue("default", getNode());
        return defaultValue != null ? defaultValue.toString() : null;
    }
}
