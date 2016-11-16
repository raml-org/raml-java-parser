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

import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.util.NodeSelector;

import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.ITEMS_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MAX_ITEMS_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MIN_ITEMS_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.UNIQUE_ITEMS_KEY_NAME;
import static org.raml.yagi.framework.util.NodeSelector.selectBooleanValue;
import static org.raml.yagi.framework.util.NodeSelector.selectIntValue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ArrayResolvedType extends XmlFacetsCapableType
{

    private ResolvedType items;
    private Boolean uniqueItems;
    private Integer minItems;
    private Integer maxItems;

    public ArrayResolvedType(TypeExpressionNode node, XmlFacets xmlFacets, ResolvedType items, Boolean uniqueItems, Integer minItems, Integer maxItems, ResolvedCustomFacets customFacets)
    {
        super(node, xmlFacets, customFacets);
        this.items = items;
        this.uniqueItems = uniqueItems;
        this.minItems = minItems;
        this.maxItems = maxItems;
    }

    public ArrayResolvedType(TypeExpressionNode node, ResolvedType items)
    {
        this(node);
        this.items = items;
    }

    public ArrayResolvedType(TypeExpressionNode node)
    {
        super(node, new ResolvedCustomFacets(MIN_ITEMS_KEY_NAME, MAX_ITEMS_KEY_NAME, UNIQUE_ITEMS_KEY_NAME, ITEMS_KEY_NAME));
    }

    protected ArrayResolvedType copy()
    {
        return new ArrayResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), items, uniqueItems, minItems, maxItems, customFacets.copy());
    }


    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(grammar.uniqueItemsField())
                                                   .add(grammar.itemsField())
                                                   .add(grammar.minItemsField())
                                                   .add(grammar.maxItemsField())
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());

    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        // Validate that is can be overwritten by
        final ArrayResolvedType result = copy();
        result.customFacets = result.customFacets.overwriteFacets(from);
        result.setMinItems(selectIntValue(MIN_ITEMS_KEY_NAME, from));
        result.setMaxItems(selectIntValue(MAX_ITEMS_KEY_NAME, from));
        result.setUniqueItems(selectBooleanValue(UNIQUE_ITEMS_KEY_NAME, from));
        final Node items = NodeSelector.selectFrom(ITEMS_KEY_NAME, from);
        if (items != null && items instanceof TypeDeclarationNode)
        {
            result.setItems(((TypeDeclarationNode) items).getResolvedType());
        }
        overwriteFacets(result, from);
        return result;
    }

    @Override
    public boolean doAccept(ResolvedType resolvedType)
    {
        if (resolvedType instanceof ArrayResolvedType)
        {
            return items.accepts(((ArrayResolvedType) resolvedType).getItems());
        }
        else
        {
            return false;
        }
    }

    public void validateState()
    {
        int min = minItems != null ? minItems : 0;
        int max = maxItems != null ? maxItems : Integer.MAX_VALUE;
        if (max < min)
        {
            getTypeDeclarationNode().replaceWith(RamlErrorNodeFactory.createInvalidFacetState(getTypeName(), "maxItems must be greater than or equal to minItems."));
        }

        if (getItems() instanceof SchemaBasedResolvedType)
        {
            getTypeDeclarationNode().replaceWith(RamlErrorNodeFactory.createInvalidFacetState(getItems().getTypeName(), "array type cannot be of an external type."));
        }
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
        result.customFacets = result.customFacets.mergeWith(with.customFacets());
        return mergeFacets(result, with);
    }


    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitArray(this);
    }

    @Nonnull
    public ResolvedType getItems()
    {
        if (items == null)
        {
            // If array is not typed (either through 'items' or an expression) then items in the array can be anything
            items = new AnyResolvedType(getTypeDeclarationNode());
        }
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

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.ARRAY.getType();
    }

}
