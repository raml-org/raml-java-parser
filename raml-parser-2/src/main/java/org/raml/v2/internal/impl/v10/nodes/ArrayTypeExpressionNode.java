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

import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.ArrayResolvedType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayTypeExpressionNode extends AbstractRamlNode implements TypeExpressionNode
{

    public ArrayTypeExpressionNode()
    {
    }

    public ArrayTypeExpressionNode(Node of)
    {
        this.addChild(of);
    }

    private ArrayTypeExpressionNode(ArrayTypeExpressionNode arrayTypeTypeNode)
    {
        super(arrayTypeTypeNode);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ArrayTypeExpressionNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.String;
    }

    @Nullable
    public TypeExpressionNode of()
    {
        if (!getChildren().isEmpty() && getChildren().get(0) instanceof TypeExpressionNode)
        {
            return (TypeExpressionNode) getChildren().get(0);
        }
        else
        {
            final Node typeDeclaration = getTypeDeclaration();
            if (typeDeclaration != null)
            {
                final Node item = typeDeclaration.get("item");
                return (TypeExpressionNode) item;
            }
            else
            {
                return null;
            }
        }
    }

    private Node getTypeDeclaration()
    {
        return org.raml.yagi.framework.util.NodeUtils.getAncestor(this, 2);
    }

    @Override
    @Nullable
    public ResolvedType generateDefinition(TypeDeclarationNode node)
    {
        final TypeExpressionNode of = of();
        if (of != null)
        {
            return new ArrayResolvedType(node, of.generateDefinition(node));
        }
        else
        {
            return null;
        }
    }
}
