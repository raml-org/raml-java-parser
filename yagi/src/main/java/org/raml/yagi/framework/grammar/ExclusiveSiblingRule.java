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
package org.raml.yagi.framework.grammar;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

public class ExclusiveSiblingRule extends Rule
{


    private String value;
    private Set<String> notAllowedSiblings;

    public ExclusiveSiblingRule(String value, Set<String> notAllowedSiblings)
    {
        this.value = value;
        this.notAllowedSiblings = notAllowedSiblings;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Lists.newArrayList();
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode && ((StringNode) node).getValue().equals(value);
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!(node instanceof StringNode))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.String);
        }
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidValue(node, value);
        }
        if (matchesSiblings(node))
        {
            return ErrorNodeFactory.createInvalidSiblingsValue(node, notAllowedSiblings);
        }
        return createNodeUsingFactory(node, ((StringNode) node).getValue());
    }

    private boolean matchesSiblings(Node node)
    {
        if (node.getParent() == null || node.getParent().getParent() == null)
        {
            return false;
        }
        Node grandParent = node.getParent().getParent();
        for (String sibling : notAllowedSiblings)
        {
            if (grandParent.get(sibling) != null)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public String getDescription()
    {
        return "\"" + value + "\"";
    }

}
