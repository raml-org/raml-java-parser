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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.utils.NodeSelector;

import static org.raml.v2.internal.utils.NodeSelector.selectBooleanValue;
import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;

public class ArrayResolvedType extends XmlFacetsCapableType
{
    private ResolvedType items;
    private Boolean uniqueItems;
    private Integer minItems;
    private Integer maxItems;

    public ArrayResolvedType(XmlFacets xmlFacets, ResolvedType items, Boolean uniqueItems, Integer minItems, Integer maxItems)
    {
        super(xmlFacets);
        this.items = items;
        this.uniqueItems = uniqueItems;
        this.minItems = minItems;
        this.maxItems = maxItems;
    }

    public ArrayResolvedType()
    {
    }

    public ArrayResolvedType(ResolvedType items)
    {
        this.items = items;
    }

    private ArrayResolvedType copy()
    {
        return new ArrayResolvedType(getXmlFacets().copy(), items, uniqueItems, minItems, maxItems);
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final ArrayResolvedType result = copy();
        result.setMinItems(selectIntValue("minItems", from));
        result.setMaxItems(selectIntValue("maxItems", from));
        result.setUniqueItems(selectBooleanValue("uniqueItems", from));
        final Node items = NodeSelector.selectFrom("items", from);
        if (items != null && items instanceof TypeDeclarationNode)
        {
            result.setItems(((TypeDeclarationNode) items).getResolvedType());
        }
        return overwriteFacets(result, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final ArrayResolvedType result = copy();
        if (with instanceof ArrayResolvedType)
        {
            result.setMinItems(((ArrayResolvedType) with).getMinItems());
            result.setMaxItems(((ArrayResolvedType) with).getMaxItems());
            result.setUniqueItems(((ArrayResolvedType) with).getUniqueItems());
            result.setItems(((ArrayResolvedType) with).getItems());
        }
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitArray(this);
    }

    public ResolvedType getItems()
    {
        return items;
    }

    private void setItems(ResolvedType items)
    {
        if (items != null)
        {
            this.items = items;
        }
    }

    public Boolean getUniqueItems()
    {
        return uniqueItems;
    }

    private void setUniqueItems(Boolean uniqueItems)
    {
        if (uniqueItems != null)
        {
            this.uniqueItems = uniqueItems;
        }
    }

    public Integer getMinItems()
    {
        return minItems;
    }

    private void setMinItems(Integer minItems)
    {
        if (minItems != null)
        {
            this.minItems = minItems;
        }
    }

    public Integer getMaxItems()
    {
        return maxItems;
    }

    private void setMaxItems(Integer maxItems)
    {
        if (maxItems != null)
        {
            this.maxItems = maxItems;
        }
    }
}
