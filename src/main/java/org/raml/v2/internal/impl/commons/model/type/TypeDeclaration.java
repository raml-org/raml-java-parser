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
package org.raml.v2.internal.impl.commons.model.type;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.framework.model.ModelUtils;
import org.raml.v2.internal.impl.commons.model.Annotable;
import org.raml.v2.internal.impl.commons.model.RamlValidationResult;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.AnyResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeToSchemaVisitor;
import org.raml.v2.internal.utils.NodeSelector;

import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.singletonList;

public abstract class TypeDeclaration<T extends ResolvedType> extends Annotable
{

    private KeyValueNode node;
    private T resolvedType;

    public TypeDeclaration(KeyValueNode node, T resolvedType)
    {
        this.node = node;
        this.resolvedType = resolvedType;
    }

    public T getResolvedType()
    {
        return resolvedType;
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }

    public String name()
    {
        if (node instanceof PropertyNode)
        {
            return ((PropertyNode) node).getName();
        }
        else
        {
            return ((StringNode) node.getKey()).getValue();
        }
    }

    public String schemaContent()
    {
        if (node.getValue() instanceof TypeDeclarationNode)
        {
            final TypeDeclarationNode value = (TypeDeclarationNode) node.getValue();
            final List<TypeExpressionNode> baseTypes = value.getBaseTypes();
            if (!baseTypes.isEmpty())
            {
                final ResolvedType resolvedType = baseTypes.get(0).generateDefinition(value);
                if (resolvedType instanceof SchemaBasedResolvedType)
                {
                    return ((SchemaBasedResolvedType) resolvedType).getSchemaValue();
                }
            }
        }
        return null;
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
        if (node instanceof PropertyNode)
        {
            return ((PropertyNode) node).isRequired();
        }
        else
        {
            return NodeSelector.selectType("required", getNode(), true);
        }
    }

    public String defaultValue()
    {
        Object defaultValue = ModelUtils.getSimpleValue("default", getNode());
        return defaultValue != null ? defaultValue.toString() : null;
    }

    public String toXmlSchema()
    {
        if (getResolvedType() instanceof SchemaBasedResolvedType || getResolvedType() instanceof AnyResolvedType)
        {
            return null;
        }
        final TypeToSchemaVisitor typeToSchemaVisitor = new TypeToSchemaVisitor();
        typeToSchemaVisitor.transform(name(), getResolvedType());
        final XmlSchema schema = typeToSchemaVisitor.getSchema();
        final StringWriter writer = new StringWriter();
        schema.write(writer);
        return writer.toString();
    }
}
