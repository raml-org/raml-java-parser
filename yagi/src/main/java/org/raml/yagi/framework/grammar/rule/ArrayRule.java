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
package org.raml.yagi.framework.grammar.rule;


import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.NullNodeImpl;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.ParsingContextType;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.yagi.framework.util.NodeUtils;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayRule extends Rule
{

    private Rule of;
    private boolean strict = false;

    public ArrayRule(Rule of)
    {
        this.of = of;
    }

    public ArrayRule(Rule of, boolean strict)
    {
        this.of = of;
        this.strict = strict;
    }

    public Rule of()
    {
        return of;
    }

    public void of(Rule of)
    {
        this.of = of;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        // Array does not have any children then we need to create a dummy one and inject into the tree
        final NullNodeImpl nullNode = new NullNodeImpl();
        node.addChild(nullNode);
        final List<Suggestion> suggestions = of.getSuggestions(nullNode, context);
        final List<Suggestion> result = new ArrayList<>();
        for (Suggestion suggestion : suggestions)
        {
            if (node instanceof ArrayNode && !((ArrayNode) node).isJsonStyle())
            {
                result.add(suggestion);
            }
            else if (context.getContextType() == ParsingContextType.VALUE)
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
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, ParsingContext context)
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
    public List<Rule> getChildren()
    {
        return Collections.singletonList(of);
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        boolean matches = node instanceof ArrayNode;
        if (matches && strict)
        {
            for (Node child : node.getChildren())
            {
                if (!of.matches(child))
                {
                    return false;
                }
            }
        }
        return matches;
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
