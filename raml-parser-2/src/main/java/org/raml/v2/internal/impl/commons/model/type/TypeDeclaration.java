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

import static java.util.Collections.singletonList;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.ws.commons.schema.XmlSchema;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.model.Annotable;
import org.raml.v2.internal.impl.commons.model.RamlValidationResult;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.v2.internal.impl.v10.nodes.NamedTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.v10.type.AnyResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeToSchemaVisitor;
import org.raml.v2.internal.impl.v10.type.XmlFacetsCapableType;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.util.NodeSelector;

public abstract class TypeDeclaration<T extends ResolvedType> extends Annotable
{

    private static final List<String> GLOBAL_NAMED_TYPE_DECLARATION_NODE_NAMES = Arrays.asList("types", "annotationTypes", "baseUriParameters");
    private static final List<String> LOCAL_NAMED_TYPE_DECLARATION_NODE_NAMES = Arrays.asList("headers", "queryParameters", "queryString", "uriParameters");

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

    public String type()
    {
        return getTypeExpression(getTypeNode());
    }

    private String getTypeExpression(Node typeNode)
    {
        if (typeNode instanceof TypeExpressionNode)
        {
            return ((TypeExpressionNode) typeNode).getTypeExpressionText();
        }
        if (typeNode.getSource() instanceof StringNode)
        {
            return ((StringNode) typeNode.getSource()).getValue();
        }
        return null;
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
        final TypeDeclarationNode node = (TypeDeclarationNode) getNode();
        final ResourceLoader resourceLoader = node.getStartPosition().getResourceLoader();
        final ExampleValidationPhase exampleValidationPhase = new ExampleValidationPhase(resourceLoader);
        final Node validate = exampleValidationPhase.validate(node, payload);
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
        Object defaultValue = NodeSelector.selectType("default", getNode(), null);
        return defaultValue != null ? defaultValue.toString() : null;
    }

    public String toXmlSchema()
    {
        if (getResolvedType() instanceof SchemaBasedResolvedType || getResolvedType() instanceof AnyResolvedType || getResolvedType() == null)
        {
            return null;
        }
        final TypeToSchemaVisitor typeToSchemaVisitor = new TypeToSchemaVisitor();
        typeToSchemaVisitor.transform(rootElementName(), getResolvedType());
        final XmlSchema schema = typeToSchemaVisitor.getSchema();
        final StringWriter writer = new StringWriter();
        schema.write(writer);
        return writer.toString();
    }

    public String rootElementName()
    {
        if (resolvedType instanceof XmlFacetsCapableType)
        {
            // an XML name facet overrides any other possibility for the root XML element
            String xmlName = ((XmlFacetsCapableType) resolvedType).getXmlFacets().getName();
            if (xmlName != null)
            {
                return xmlName;
            }
        }

        if (!(node instanceof PropertyNode) && !isGlobalNamedTypeDeclarationNode() && !isLocalNamedTypeDeclarationNode())
        {
            // type reference
            final Node value = node.getValue();
            if (value.get("properties") != null || value.get("facets") != null || !(getTypeNode() instanceof NamedTypeExpressionNode))
            {
                // type is extending somehow supertype or inline declaration
                return "root";
            }
            else
            {
                // type is just referencing an existing one
                return ((NamedTypeExpressionNode) getTypeNode()).getRefName();
            }
        }
        else
        {
            // type declaration
            return name();
        }
    }

    private Node getTypeNode()
    {
        final Node value = node.getValue();
        final Node type = value.get("type");
        if (type != null)
        {
            return type;
        }
        else
        {
            return value.get("schema");
        }
    }

    /**
     * True if the node is a global named type declaration as opposed to an inline/anonymous declaration. This is computed based on the parent of the current type declaration node, i.e.
     * if its parent is one of the global named type declaration nodes possible.
     * 
     * @return <code>true</code> if the node is a global named type declaration as opposed to an inline/anonymous declaration, <code>false</code> otherwise
     */
    private boolean isGlobalNamedTypeDeclarationNode()
    {
        final Node parent = node.getParent();
        final Node rootNode = node.getRootNode();

        return Iterables.any(GLOBAL_NAMED_TYPE_DECLARATION_NODE_NAMES, new Predicate<String>()
        {

            @Override
            public boolean apply(final String name)
            {
                return parent == rootNode.get(name);
            }
        });
    }

    /**
     * True if the node is a local named type declaration as opposed to an inline/anonymous declaration. This is computed based on the parent of the current type declaration node, i.e.
     * if its parent is one of the local named type declaration nodes possible.
     * 
     * @return <code>true</code> if the node is a local named type declaration as opposed to an inline/anonymous declaration, <code>false</code> otherwise
     */
    private boolean isLocalNamedTypeDeclarationNode()
    {
        final Node parent = node.getParent();
        // tries to get the grandparent of the current node to retrieve the same node based on the known local named declaration node names
        final Node baseNode = parent.getParent().getParent();

        return Iterables.any(LOCAL_NAMED_TYPE_DECLARATION_NODE_NAMES, new Predicate<String>()
        {

            @Override
            public boolean apply(final String name)
            {
                return parent == baseNode.get(name);
            }
        });
    }
}
