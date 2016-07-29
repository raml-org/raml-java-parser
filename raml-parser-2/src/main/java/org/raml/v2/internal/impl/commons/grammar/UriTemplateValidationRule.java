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
package org.raml.v2.internal.impl.commons.grammar;

import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.snakeyaml.SYStringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.v2.internal.utils.UriTemplateValidation;

import javax.annotation.Nonnull;
import java.util.List;

public class UriTemplateValidationRule extends Rule
{
    private Rule rule;

    public UriTemplateValidationRule(Rule rule)
    {
        super();
        this.rule = rule;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return rule.matches(node);
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        Node apply = rule.apply(node);
        if (node instanceof SYStringNode)
        {
            if (!UriTemplateValidation.isBalanced(((SYStringNode) node).getValue()))
            {
                apply = RamlErrorNodeFactory.createInvalidUriTemplate();
            }
        }

        return apply;
    }

    @Override
    public String getDescription()
    {
        return rule.getDescription();
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return rule.getSuggestions(node, context);
    }
}
