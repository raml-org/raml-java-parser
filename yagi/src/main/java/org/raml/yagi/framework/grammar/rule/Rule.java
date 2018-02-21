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

import org.raml.yagi.framework.grammar.BaseGrammar;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Rule
{
    @Nullable
    private NodeFactory factory;
    private String ruleName = getClass().getSimpleName();

    protected Rule()
    {
        if (Boolean.getBoolean("yagui.rule.debug"))
        {
            final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace)
            {
                final String className = stackTraceElement.getClassName();
                if (className.contains("Grammar") && !className.endsWith(BaseGrammar.class.getSimpleName()))
                {
                    ruleName = stackTraceElement.getMethodName();
                    break;
                }
            }
        }
    }

    public List<? extends Rule> getChildren()
    {
        return Collections.emptyList();
    }

    public Rule named(String ruleName)
    {
        this.ruleName = ruleName;
        return this;
    }

    public String ruleName()
    {
        return ruleName;
    }

    @Override
    public String toString()
    {
        return ruleName;
    }

    /**
     * Check if the current rule matches the specified node
     *
     * @param node The node to check with
     * @return True if it matches otherwise false
     */
    public abstract boolean matches(@Nonnull Node node);

    /**
     * Applies the rule to the node using the following criteria
     * - if rule does not match returns an ErrorNode and stops processing
     * - if rule matches, applies rules to children
     * - if rule contains a NodeFactory, returns the result of it
     * - else returns node
     * <p/>
     * Only structure rules ({@link ObjectRule}, {@link ArrayRule}, {@link KeyValueRule}) replace child nodes.
     *
     * @param node The current node
     * @return the result of the factory or the current node
     */
    @Nonnull
    public abstract Node apply(@Nonnull Node node);

    /**
     * Returns a description of this rule
     * @return the description
     */
    public abstract String getDescription();

    @Nullable
    public NodeFactory getFactory()
    {
        return factory;
    }

    @Nonnull
    protected Node createNodeUsingFactory(@Nonnull Node currentNode, Object... args)
    {
        if (getFactory() != null)
        {
            Node newNode = getFactory().create(currentNode, args);
            if (!newNode.getClass().isAssignableFrom(currentNode.getClass()))
            {
                return newNode;
            }
        }
        return currentNode;
    }

    /**
     * Sets the clazz of the node that is going to be created to replaced the matched node of this rule.
     * This is only applied if the rule matches
     * @param clazz The class of the node
     * @return this
     */
    public Rule then(Class<? extends Node> clazz)
    {
        this.factory = new ClassNodeFactory(clazz);
        return this;
    }

    /**
     * Sets the factory of the node that is going to be created to replaced the matched node of this rule.
     * This is only applied if the rule matches
     * @param factory The class of the node
     * @return this
     */
    public Rule then(NodeFactory factory)
    {
        this.factory = factory;
        return this;
    }

    public Rule cleanFactory()
    {
        this.factory = null;
        return this;
    }

    /**
     * Returns the list of suggestions after navigating through the path
     * @param pathToRoot The path of nodes to get of the node from where we want the suggestions
     * @param context The parse context
     * @return The list of suggestions
     */
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, ParsingContext context)
    {
        if (!pathToRoot.isEmpty())
        {
            return getSuggestions(pathToRoot.get(0), context);
        }
        else
        {
            return Collections.emptyList();
        }
    }

    /**
     * Returns the suggestions of this specific rule
     * @param node The node
     * @param context The parse context
     * @return The list of suggestions for the specified rule
     */
    @Nonnull
    public abstract List<Suggestion> getSuggestions(Node node, ParsingContext context);
}
