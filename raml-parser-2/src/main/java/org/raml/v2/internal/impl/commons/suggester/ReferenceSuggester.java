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
package org.raml.v2.internal.impl.commons.suggester;

import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ReferenceSuggester
{

    private String referenceKey;

    public ReferenceSuggester(String referenceKey)
    {
        this.referenceKey = referenceKey;
    }

    @Nonnull
    public List<Suggestion> getSuggestions(Node node)
    {
        final List<Suggestion> result = new ArrayList<>();
        Node contextNode = node.getRootNode();
        if (node instanceof StringNode)
        {
            final String value = ((StringNode) node).getValue();
            final String[] library = value.split("\\.");
            for (int i = 0; i < library.length - 1 && contextNode != null; i++)
            {
                contextNode = NodeSelector.selectFrom(Raml10Grammar.USES_KEY_NAME + "/*/" + library[i], contextNode);
            }
        }
        if (contextNode != null)
        {
            final Node selectFrom = NodeSelector.selectFrom(referenceKey, contextNode);
            addSuggestions(result, selectFrom);
            final Node libraries = NodeSelector.selectFrom(Raml10Grammar.USES_KEY_NAME, contextNode);
            addSuggestions(result, libraries);
        }
        return result;
    }

    private void addSuggestions(List<Suggestion> result, Node selectFrom)
    {
        if (selectFrom != null)
        {
            if (selectFrom instanceof ObjectNode)
            {
                collectSuggestions(result, selectFrom);
            }
            else if (selectFrom instanceof ArrayNode)
            {
                final List<Node> children = selectFrom.getChildren();
                for (Node child : children)
                {
                    collectSuggestions(result, child);
                }
            }
        }
    }

    private void collectSuggestions(List<Suggestion> result, Node selectFrom)
    {
        final List<Node> children = selectFrom.getChildren();
        for (Node child : children)
        {
            if (!child.getChildren().isEmpty())
            {
                final String value = child.getChildren().get(0).toString();
                final Node grandchild = child.getChildren().get(1);

                if (grandchild instanceof ArrayNode) // in case of a multiple inherited type
                {
                    final List<Node> referenceNodes = grandchild.getChildren();
                    for (Node referenceNode : referenceNodes)
                    {
                        collectReferenceNodeSuggestions(result, value, referenceNode);
                    }
                }
                else
                {
                    collectReferenceNodeSuggestions(result, value, grandchild);
                }
            }
        }
    }

    private void collectReferenceNodeSuggestions(List<Suggestion> result, String value, Node grandchild)
    {
        final Node description = NodeSelector.selectFrom("usage", grandchild);
        String descriptionText = "";
        if (description != null)
        {
            descriptionText = description.toString();
        }
        else
        {
            final Node usage = NodeSelector.selectFrom("description", grandchild);
            if (usage != null)
            {
                descriptionText = usage.toString();
            }
        }
        result.add(new DefaultSuggestion(value, descriptionText, value));
    }
}
