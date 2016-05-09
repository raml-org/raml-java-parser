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
package org.raml.v2.internal.framework.grammar.rule;

import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.RamlParsingContextType;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.utils.NodeUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayRule extends Rule
{

    private Rule of;

    public ArrayRule(Rule of)
    {
        this.of = of;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        final List<Suggestion> suggestions = of.getSuggestions(node, context);
        final List<Suggestion> result = new ArrayList<>();
        for (Suggestion suggestion : suggestions)
        {
            if (node instanceof ArrayNode && !((ArrayNode) node).isJsonStyle())
            {
                result.add(suggestion);
            }
            else if (context.getContextType() == RamlParsingContextType.VALUE)
            {
                final String prefix = "\n" + NodeUtils.computeColumnForChild(node.getParent());
                result.add(suggestion.withValue("- " + suggestion.getValue()).withPrefix(prefix));
            }
            else
            {
                result.add(suggestion.withValue("- " + suggestion.getValue()));
            }
        }
        return result;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, RamlParsingContext context)
    {
        if (pathToRoot.isEmpty())
        {
            return Collections.emptyList();
        }
        else
        {
            final Node mappingNode = pathToRoot.get(0);
            switch (pathToRoot.size())
            {
            case 1:
                return getSuggestions(mappingNode, context);
            default:
                return of.getSuggestions(pathToRoot.subList(1, pathToRoot.size()), context);
            }
        }
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof ArrayNode;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.Array);
        }
        else
        {
            Node result = createNodeUsingFactory(node);
            final List<Node> children = node.getChildren();
            for (Node child : children)
            {
                if (of.matches(child))
                {
                    final Node transform = of.apply(child);
                    child.replaceWith(transform);
                }
                else
                {
                    child.replaceWith(ErrorNodeFactory.createInvalidArrayElement(child));
                }
            }
            return result;
        }
    }

    @Override
    public String getDescription()
    {
        return "Array[" + of.getDescription() + "]";
    }
}
