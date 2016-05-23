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
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.utils.NodeSelector;

import static org.raml.v2.internal.utils.NodeSelector.selectBooleanValue;
import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;

public class ArrayTypeDefinition implements TypeDefinition
{
    private TypeDefinition items;
    private Boolean uniqueItems;
    private Integer minItems;
    private Integer maxItems;

    public ArrayTypeDefinition(TypeDefinition items, Boolean uniqueItems, Integer minItems, Integer maxItems)
    {
        this.items = items;
        this.uniqueItems = uniqueItems;
        this.minItems = minItems;
        this.maxItems = maxItems;
    }

    public ArrayTypeDefinition()
    {
    }

    public ArrayTypeDefinition(TypeDefinition items)
    {
        this.items = items;
    }

    private ArrayTypeDefinition copy()
    {
        return new ArrayTypeDefinition(items, uniqueItems, minItems, maxItems);
    }

    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        final ArrayTypeDefinition result = copy();
        result.setMinItems(selectIntValue("minItems", from));
        result.setMaxItems(selectIntValue("maxItems", from));
        result.setUniqueItems(selectBooleanValue("uniqueItems", from));
        final Node items = NodeSelector.selectFrom("items", from);
        if (items != null && items instanceof TypeDeclarationNode)
        {
            result.setItems(((TypeDeclarationNode) items).getTypeDefinition());
        }
        return result;
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        final ArrayTypeDefinition result = copy();
        if (with instanceof ArrayTypeDefinition)
        {
            result.setMinItems(((ArrayTypeDefinition) with).getMinItems());
            result.setMaxItems(((ArrayTypeDefinition) with).getMaxItems());
            result.setUniqueItems(((ArrayTypeDefinition) with).getUniqueItems());
            result.setItems(((ArrayTypeDefinition) with).getItems());
        }
        return result;
    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
    {
        return visitor.visitArray(this);
    }

    public TypeDefinition getItems()
    {
        return items;
    }

    private void setItems(TypeDefinition items)
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
