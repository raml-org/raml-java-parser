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
package org.raml.v2.internal.impl.commons.type;

import java.util.List;

import org.raml.v2.internal.impl.commons.nodes.FacetNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.yagi.framework.util.NodeUtils;

public abstract class AbstractExternalType extends BaseType implements SchemaBasedResolvedType
{

    private final String schemaValue;
    private final String schemaPath;
    private final String internalFragment;

    public AbstractExternalType(TypeExpressionNode from, String schemaValue, String schemaPath, String internalFragment)
    {
        super(getExternalTypeName(from), from, new ResolvedCustomFacets());
        this.schemaValue = schemaValue;
        this.schemaPath = schemaPath;
        this.internalFragment = internalFragment;
    }

    public AbstractExternalType(AbstractExternalType externalType)
    {
        super(externalType.getTypeName(), externalType.getTypeExpressionNode(), new ResolvedCustomFacets());
        this.schemaValue = externalType.schemaValue;
        this.schemaPath = externalType.schemaPath;
        this.internalFragment = externalType.internalFragment;
    }

    protected abstract AbstractExternalType copy();

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        return copy();
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        return copy();
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        List<FacetNode> facetNodes = from.findDescendantsWith(FacetNode.class);
        for (FacetNode facetNode : facetNodes)
        {
            facetNode.replaceWith(RamlErrorNodeFactory.createInvalidFacetForType(facetNode, getClass().getSimpleName()));
        }
    }

    @Override
    public void validateState()
    {
        super.validateState();
    }

    public String getSchemaValue()
    {
        return schemaValue;
    }

    public String getSchemaPath()
    {
        return schemaPath;
    }

    public String getInternalFragment()
    {
        return internalFragment;
    }

    public static String getExternalTypeName(TypeExpressionNode node)
    {
        TypeDeclarationField ancestor = NodeUtils.getAncestor(node, TypeDeclarationField.class);
        if (ancestor != null)
        {
            return ancestor.getName();
        }
        else
        {
            return "Schema";
        }
    }
}
