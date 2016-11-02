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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.type.TypeToRuleVisitor;
import org.raml.yagi.framework.grammar.rule.KeyValueRule;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.grammar.rule.StringValueRule;

public class CustomFacetDefinitionNode extends PropertyNode
{
    public CustomFacetDefinitionNode()
    {
    }

    CustomFacetDefinitionNode(CustomFacetDefinitionNode node)
    {
        super(node);
    }

    public TypeDeclarationNode getFacetType()
    {
        return (TypeDeclarationNode) getValue();
    }

    public String getFacetName()
    {
        return PropertyUtils.getName(this);
    }

    public boolean isRequired()
    {
        return PropertyUtils.isRequired(this);
    }

    public KeyValueRule getFacetRule()
    {
        final TypeToRuleVisitor typeToRuleVisitor = new TypeToRuleVisitor(new DefaultResourceLoader());
        final Rule value = getFacetType().getResolvedType().visit(typeToRuleVisitor);
        return new KeyValueRule(new StringValueRule(getFacetName()), value);
    }

    @Override
    public CustomFacetDefinitionNode copy()
    {
        return new CustomFacetDefinitionNode(this);
    }
}
