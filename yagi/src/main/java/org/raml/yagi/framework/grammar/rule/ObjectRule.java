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

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.ParsingContextType;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.yagi.framework.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectRule extends Rule
{

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private List<KeyValueRule> fields;
    @Nullable
    private ConditionalRules conditionalRules;
    private ExclusiveKeys exclusiveKeys;
    private boolean strict = false;
    private boolean allowsAdditionalProperties = false;


    public ObjectRule()
    {
        this.fields = new ArrayList<>();
    }

    @Override
    public List<? extends Rule> getChildren()
    {
        final ArrayList<Rule> children = new ArrayList<>();
        children.addAll(fields);
        if (conditionalRules != null)
        {
            children.addAll(conditionalRules.getChildren());
        }
        return children;
    }

    public ObjectRule(boolean strict)
    {
        this();
        this.strict = strict;
    }

    public ObjectRule named(String ruleName)
    {
        super.named(ruleName);
        return this;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        List<Suggestion> result = new ArrayList<>();
        // In cases where the node we are trying to suggest is a null node just return the known fields else try guessing the conditional ones
        final List<KeyValueRule> fieldRules = node instanceof NullNode ? fields : getAllFieldRules(node);
        for (KeyValueRule rule : fieldRules)
        {
            if (rule.repeated() || findMatchingNode(rule, node.getChildren()) == null)
            {
                if (context.getContextType() == ParsingContextType.VALUE)
                {
                    final List<Suggestion> keySuggestions = rule.getKeySuggestions(node, context);
                    final Node editing = NodeUtils.searchNodeAt(NodeUtils.traverseToRoot(node), context.getLocation());
                    final String prefix = "\n" + NodeUtils.computeColumnForChild(editing instanceof NullNode ? editing.getParent() : editing);
                    for (Suggestion keySuggestion : keySuggestions)
                    {
                        result.add(keySuggestion.withPrefix(prefix));
                    }
                }
                else
                {
                    // We return the suggestions of the key
                    result.addAll(rule.getKeySuggestions(node, context));
                }
            }
        }
        return result;
    }

    @Nullable
    private Node findMatchingNode(KeyValueRule rule, List<Node> children)
    {
        for (Node child : children)
        {
            if (rule.matches(child) && matchesKey(child, rule) && matchesValue(child, rule))
            {
                return child;
            }
        }

        return null;
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        boolean isObjectNode = node instanceof ObjectNode;
        if (!strict)
        {
            return isObjectNode;
        }
        else
        {
            return isObjectNode && allChildrenMatch(node);
        }
    }

    private boolean allChildrenMatch(Node node)
    {
        List<Node> children = node.getChildren();
        final List<KeyValueRule> allFieldRules = getAllFieldRules(node);

        Set<Node> unmatchedChildren = new HashSet<>(children);

        for (KeyValueRule rule : allFieldRules)
        {
            // If the key is required, but it does not match any rule
            if (rule.isRequired(node) && !unmatchedChildren.remove(findMatchingNode(rule, children)))
            {
                return false;
            }
        }

        // All children that are not required and additional properties will be in unmatchedChildren
        for (Node child : unmatchedChildren)
        {
            // If it matches the key, but does not match the rule
            if (childMatchesAnyKey(child, allFieldRules) && !childMatchesAnyRule(child, allFieldRules))
            {
                return false;
            }
        }

        return true;
    }

    private boolean childMatchesAnyKey(Node child, List<KeyValueRule> allFieldRules)
    {
        for (KeyValueRule rule : allFieldRules)
        {
            if (matchesKey(child, rule))
            {
                return true;
            }
        }

        return false;
    }

    private boolean childMatchesAnyRule(Node child, List<KeyValueRule> allFieldRules)
    {
        for (KeyValueRule rule : allFieldRules)
        {
            if (rule.matches(child) && matchesKey(child, rule) && matchesValue(child, rule))
            {
                return true;
            }
        }

        return false;
    }

    private boolean matchesKey(Node child, KeyValueRule rule)
    {
        return rule.getKeyRule().matches(child.getChildren().get(0));
    }

    private boolean matchesValue(Node child, KeyValueRule rule)
    {
        return rule.getValueRule().matches(child.getChildren().get(1));
    }

    @Nonnull
    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!matches(node))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.Object);
        }
        else
        {

            final List<Node> children = node.getChildren();
            final List<KeyValueRule> allFieldRules = getAllFieldRules(node);
            final List<KeyValueRule> nonMatchingRules = new ArrayList<>(allFieldRules);

            for (Node child : children)
            {
                final Rule matchingRule = findMatchingRule(allFieldRules, child);
                if (matchingRule != null)
                {
                    nonMatchingRules.remove(matchingRule);
                    final Node newChild = matchingRule.apply(child);
                    child.replaceWith(newChild);
                }
                else
                {
                    if (!allowsAdditionalProperties)
                    {
                        final Collection<String> options = Collections2.transform(allFieldRules, new Function<KeyValueRule, String>()
                        {
                            @Override
                            public String apply(KeyValueRule rule)
                            {
                                return rule.getKeyRule().getDescription();
                            }
                        });
                        child.replaceWith(ErrorNodeFactory.createUnexpectedKey(((KeyValueNode) child).getKey(), new TreeSet<>(options)));
                    }
                }
            }

            for (KeyValueRule rule : nonMatchingRules)
            {
                if (rule.isRequired(node))
                {
                    node.addChild(ErrorNodeFactory.createRequiredValueNotFound(node, rule.getKeyRule()));
                }
                else
                {
                    rule.applyDefault(node);
                }
            }

            validateKeysUnique(node);
            validateExclusiveKeys(node);
            return getResult(node);
        }
    }

    private void validateKeysUnique(final Node node)
    {
        final List<Node> children = node.getChildren();

        final Set<String> gotcha = new HashSet<>();
        for (final Node child : children)
        {
            if (child instanceof KeyValueNode)
            {
                final String key = ((KeyValueNode) child).getKey().toString();
                if (gotcha.contains(key))
                {
                    child.replaceWith(new ErrorNode("Duplicated key '" + key + "'"));
                }
                else
                {
                    gotcha.add(key);
                }
            }
            else if (!(child instanceof ErrorNode))
            {
                logger.error("Child '" + child + "' not a key value node");
            }
        }
    }

    private void validateExclusiveKeys(final Node node)
    {
        if (exclusiveKeys != null)
        {
            boolean hasMatchedRule = false;
            List<Node> children = node.getChildren();
            List<Rule> keys = exclusiveKeys.getAllRules();

            for (Node child : children)
            {
                for (Rule rule : keys)
                {
                    if (child instanceof KeyValueNode)
                    {
                        Node key = ((KeyValueNodeImpl) child).getKey();

                        if (rule.matches(key))
                        {
                            if (!hasMatchedRule)
                            {
                                hasMatchedRule = true;
                            }
                            else
                            {
                                final String firstRule = keys.get(0).getDescription();
                                final String secondRule = keys.get(1).getDescription();
                                child.replaceWith(ErrorNodeFactory.createExclusiveKeys(firstRule, secondRule));
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    protected Node getResult(Node node)
    {
        return createNodeUsingFactory(node);
    }

    private List<KeyValueRule> getAllFieldRules(Node node)
    {
        if (conditionalRules != null)
        {
            // We first add the local rules and then conditional rules
            final ArrayList<KeyValueRule> rules = new ArrayList<>(fields);
            final List<KeyValueRule> rulesNode = conditionalRules.getRulesNode(node);
            rules.addAll(rulesNode);
            return rules;
        }
        else
        {
            return fields;
        }
    }

    @Nullable
    private Rule findMatchingRule(List<? extends Rule> rootRule, Node node)
    {
        for (Rule rule : rootRule)
        {
            if (rule.matches(node))
            {
                return rule;
            }
        }
        return null;
    }

    @Override
    public ObjectRule then(Class<? extends Node> clazz)
    {
        super.then(clazz);
        return this;
    }

    @Override
    public ObjectRule then(NodeFactory factory)
    {
        super.then(factory);
        return this;
    }

    /**
     * Adds a field to this object rule
     *
     * @param field The field defined to this object
     * @return this
     */
    public ObjectRule with(KeyValueRule field)
    {
        this.fields.add(field);
        return this;
    }

    /**
     * Adds all a field to this object rule
     *
     * @param fields The fields defined to this object
     * @return this
     */
    public ObjectRule withAll(KeyValueRule... fields)
    {
        if (fields != null)
        {
            this.fields.addAll(Arrays.asList(fields));
        }
        return this;
    }

    /**
     * Adds a field to this object rule at a given order
     *
     * @param field The field defined to this object
     * @param index the index
     * @return this
     */
    public ObjectRule with(int index, KeyValueRule field)
    {
        this.fields.add(index, field);
        return this;
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
                final Node node = pathToRoot.get(1);
                final Rule matchingRule = findMatchingRule(getAllFieldRules(mappingNode), node);
                return matchingRule == null ? Collections.<Suggestion> emptyList() : matchingRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()), context);
            }
        }
    }

    @Override
    public String getDescription()
    {
        return "Mapping";
    }

    public void additionalProperties(boolean allowsAdditionalProperties)
    {
        this.allowsAdditionalProperties = allowsAdditionalProperties;
    }

    /**
     * Defines conditional fields based on a condition.
     * This fields are only valid if the specified condition is
     *
     * @param conditional The conditional fields
     * @return this
     */
    public ObjectRule with(ConditionalRules conditional)
    {
        this.conditionalRules = conditional;
        return this;
    }

    /**
     * Defines the mutually exclusive fields
     *
     * @param exclusive The exclusive rules
     * @return this
     */
    public ObjectRule with(ExclusiveKeys exclusive)
    {
        this.exclusiveKeys = exclusive;
        return this;
    }

}
