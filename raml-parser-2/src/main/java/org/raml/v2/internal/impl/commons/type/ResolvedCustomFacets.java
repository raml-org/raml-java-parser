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

import org.raml.v2.internal.impl.commons.nodes.CustomFacetDefinitionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResolvedCustomFacets
{
    private Map<String, CustomFacetDefinitionNode> customFacets;
    private List<String> nativeFacets;

    public ResolvedCustomFacets(String... nativeFacets)
    {
        this(Arrays.asList(nativeFacets), new HashMap<String, CustomFacetDefinitionNode>());
    }

    public ResolvedCustomFacets(List<String> nativeFacets, Map<String, CustomFacetDefinitionNode> facets)
    {
        this.customFacets = facets;
        this.nativeFacets = nativeFacets;
    }

    /**
     * Checks if it can be inherited by the specified type
     * @param from
     * @return
     */
    public TypeDeclarationNode validate(TypeDeclarationNode from)
    {

        final List<CustomFacetDefinitionNode> customFacets = from.getCustomFacets();
        for (CustomFacetDefinitionNode customFacet : customFacets)
        {
            if (nativeFacets.contains(customFacet.getFacetName()))
            {
                customFacet.replaceWith(RamlErrorNodeFactory.createCanNotOverrideNativeFacet(customFacet.getFacetName()));
            }
            else if (this.customFacets.containsKey(customFacet.getFacetName()))
            {
                final TypeDeclarationNode parentTypeDeclaration = customFacet.findAncestorWith(TypeDeclarationNode.class);
                final String typeName = parentTypeDeclaration != null ? parentTypeDeclaration.getTypeName() : "";
                customFacet.replaceWith(RamlErrorNodeFactory.createCanNotOverrideCustomFacet(customFacet.getFacetName(), typeName));
            }
        }
        return from;
    }

    public ResolvedCustomFacets mergeWith(ResolvedCustomFacets customFacets)
    {
        final ResolvedCustomFacets copy = copy();
        copy.customFacets.putAll(customFacets.customFacets);
        return copy;
    }

    public List<Rule> getRules()
    {
        List<Rule> rules = new ArrayList<>();
        for (CustomFacetDefinitionNode facetDefinitionNode : customFacets.values())
        {
            rules.add(facetDefinitionNode.getFacetRule());
        }
        return rules;
    }

    public ResolvedCustomFacets copy()
    {
        return new ResolvedCustomFacets(nativeFacets, new HashMap<>(this.customFacets));
    }

    public ResolvedCustomFacets overwriteFacets(TypeDeclarationNode from)
    {
        final Map<String, CustomFacetDefinitionNode> facets = new HashMap<>(this.customFacets);
        final List<CustomFacetDefinitionNode> customFacets = from.getCustomFacets();
        for (CustomFacetDefinitionNode customFacet : customFacets)
        {
            facets.put(customFacet.getFacetName(), customFacet);
        }
        return new ResolvedCustomFacets(nativeFacets, facets);
    }


}
