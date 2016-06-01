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

import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.Position;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.UnionResolvedType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class UnionTypeExpressionNode extends AbstractRamlNode implements TypeExpressionNode
{

    public UnionTypeExpressionNode()
    {
    }

    private UnionTypeExpressionNode(UnionTypeExpressionNode unionTypeTypeNode)
    {
        super(unionTypeTypeNode);
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return getChildren().get(0).getStartPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return getChildren().get(getChildren().size() - 1).getEndPosition();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new UnionTypeExpressionNode(this);
    }

    public List<TypeExpressionNode> of()
    {
        final List<TypeExpressionNode> of = new ArrayList<>();
        for (Node node : getChildren())
        {
            if (node instanceof TypeExpressionNode)
            {
                of.add((TypeExpressionNode) node);
            }
        }
        return of;
    }

    @Override
    public NodeType getType()
    {
        return NodeType.String;
    }

    @Nullable
    @Override
    public ResolvedType generateDefinition(TypeDeclarationNode node)
    {
        final List<TypeExpressionNode> of = of();
        List<ResolvedType> definitions = new ArrayList<>();
        for (TypeExpressionNode typeExpressionNode : of)
        {
            definitions.add(typeExpressionNode.generateDefinition(node));
        }
        return new UnionResolvedType(node, definitions);
    }
}
