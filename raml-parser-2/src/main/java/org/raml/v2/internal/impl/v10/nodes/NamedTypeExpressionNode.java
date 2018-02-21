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
package org.raml.v2.internal.impl.v10.nodes;

import org.raml.v2.internal.impl.commons.nodes.AbstractReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NamedTypeExpressionNode extends AbstractReferenceNode implements TypeExpressionNode, SimpleTypeNode<String>
{
    private String name;

    public NamedTypeExpressionNode(String name)
    {
        this.name = name;
    }

    private NamedTypeExpressionNode(NamedTypeExpressionNode copy)
    {
        super(copy);
        this.name = copy.name;
    }

    @Nullable
    @Override
    public TypeDeclarationNode resolveReference()
    {
        for (Node contextNode : getContextNodes())
        {
            // We add the .. as the node selector selects the value and we want the key value pair
            Node node = NodeSelector.selectFrom(Raml10Grammar.TYPES_KEY_NAME + "/" + getRefName(), contextNode);

            if (node == null && contextNode != null)
            {
                if (contextNode.getRootNode() instanceof RamlDocumentNode)
                {
                    node = NodeSelector.selectFrom(Raml10Grammar.TYPES_KEY_NAME + "/" + getRefName(), contextNode.getRootNode());
                }
            }
            if (node == null)
            {
                // If is not defined in types we need to search in schemas
                node = NodeSelector.selectFrom(Raml10Grammar.SCHEMAS_KEY_NAME + "/" + getRefName(), contextNode);
            }
            if (node instanceof TypeDeclarationNode)
            {
                return (TypeDeclarationNode) node;
            }
        }
        return null;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new NamedTypeExpressionNode(this);
    }

    @Nullable
    @Override
    public ResolvedType generateDefinition()
    {
        if (getRefNode() != null)
        {
            return ((TypeDeclarationNode) getRefNode()).getResolvedType();
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getTypeExpressionText()
    {
        return getValue();
    }

    @Override
    public String toString()
    {
        return getRefName();
    }

    @Override
    public String getValue()
    {
        return name;
    }

    @Override
    public String getLiteralValue()
    {
        return name;
    }
}
