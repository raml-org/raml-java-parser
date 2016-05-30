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
package org.raml.v2.internal.impl.v10.rules;

import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.v10.type.TypeToRuleVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscriminatorBasedRule extends Rule
{
    private TypeToRuleVisitor typeToRuleVisitor;
    private Node rootElement;
    private String discriminatorProperty;
    private Map<String, Rule> typeRulesCache;


    public DiscriminatorBasedRule(TypeToRuleVisitor typeToRuleVisitor, Node rootElement, String discriminatorProperty)
    {
        this.typeToRuleVisitor = typeToRuleVisitor;
        this.rootElement = rootElement;
        this.discriminatorProperty = discriminatorProperty;
        this.typeRulesCache = new HashMap<>();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        final Node discriminatorNode = node.get(discriminatorProperty);
        if (discriminatorNode instanceof SimpleTypeNode)
        {
            return true;
        }
        return false;
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        final SimpleTypeNode discriminatorValue = (SimpleTypeNode) node.get(discriminatorProperty);
        final String literalValue = discriminatorValue.getLiteralValue();
        Rule value = findType(literalValue);
        if (value != null)
        {
            value.apply(node);
        }
        else
        {
            node.replaceWith(ErrorNodeFactory.createInvalidType(literalValue));
        }
        return node;
    }

    @Nullable
    protected Rule findType(String literalValue)
    {
        Rule value = null;
        if (!typeRulesCache.containsKey(literalValue))
        {
            final TypeDeclarationNode typeDeclaration = findTypeDeclaration(literalValue);
            if (typeDeclaration != null)
            {
                typeToRuleVisitor.resolveDiscrimintor();
                value = typeToRuleVisitor.generateRule(typeDeclaration.getResolvedType());
                typeRulesCache.put(literalValue, value);
            }
        }
        else
        {
            value = typeRulesCache.get(literalValue);
        }
        return value;
    }

    @Nullable
    protected TypeDeclarationNode findTypeDeclaration(String literalValue)
    {
        final List<TypeDeclarationField> descendantsWith = rootElement.findDescendantsWith(TypeDeclarationField.class);
        for (TypeDeclarationField typeDeclarationField : descendantsWith)
        {
            final Node typeDeclaration = typeDeclarationField.getValue();
            final Node discriminatorValue = typeDeclaration.get("discriminatorValue");
            String typeIdentifier;
            if (discriminatorValue instanceof SimpleTypeNode)
            {
                typeIdentifier = ((SimpleTypeNode) discriminatorValue).getLiteralValue();
            }
            else
            {
                typeIdentifier = ((SimpleTypeNode) typeDeclarationField.getKey()).getLiteralValue();
            }

            if (literalValue.equals(typeIdentifier))
            {
                return (TypeDeclarationNode) typeDeclaration;
            }
        }
        return null;
    }

    @Override
    public String getDescription()
    {
        return "Dynamic rule based on '" + discriminatorProperty + "'.";
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Collections.emptyList();
    }
}
