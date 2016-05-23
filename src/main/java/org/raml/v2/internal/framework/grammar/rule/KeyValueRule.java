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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.DefaultPosition;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;

public class KeyValueRule extends Rule
{

    private final Rule keyRule;
    private final Rule valueRule;
    private String description;

    private RequiredField requiredField;
    private DefaultValue defaultValue;

    public KeyValueRule(Rule keyRule, Rule valueRule)
    {
        this.keyRule = keyRule;
        this.valueRule = valueRule;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return getValueRule().getSuggestions(node, context);
    }

    @Nonnull
    public List<Suggestion> getKeySuggestions(Node node, RamlParsingContext context)
    {
        final List<Suggestion> suggestions = getKeyRule().getSuggestions(node, context);
        final List<Suggestion> result = new ArrayList<>();
        for (Suggestion suggestion : suggestions)
        {
            Suggestion keySuggest = suggestion;
            if (description != null)
            {
                keySuggest = suggestion.withDescription(description);
            }
            keySuggest = keySuggest.withValue(suggestion.getValue() + ": ");
            result.add(keySuggest);
        }
        return result;
    }

    @Override
    public List<Suggestion> getSuggestions(List<Node> pathToRoot, RamlParsingContext context)
    {
        if (!pathToRoot.isEmpty())
        {
            return valueRule.getSuggestions(pathToRoot.subList(1, pathToRoot.size()), context);
        }
        else
        {
            return super.getSuggestions(pathToRoot, context);
        }
    }


    public KeyValueRule description(String description)
    {
        this.description = description;
        return this;
    }


    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof KeyValueNode && getKeyRule().matches(((KeyValueNode) node).getKey());
    }

    public boolean repeated()
    {
        return !(getKeyRule() instanceof StringValueRule);
    }

    public Rule getKeyRule()
    {
        return keyRule;
    }

    public Rule getValueRule()
    {
        return valueRule;
    }

    public KeyValueRule then(Class<? extends Node> clazz)
    {
        super.then(clazz);
        return this;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (!(node instanceof KeyValueNode))
        {
            return ErrorNodeFactory.createInvalidType(node, NodeType.KeyValue);
        }
        else if (!getKeyRule().matches(((KeyValueNode) node).getKey()))
        {
            return getKeyRule().apply(node);
        }
        final Node result = createNodeUsingFactory(node);
        final KeyValueNode keyValueNode = (KeyValueNode) node;
        final Node key = keyValueNode.getKey();
        key.replaceWith(getKeyRule().apply(key));
        final Node value = keyValueNode.getValue();
        value.replaceWith(getValueRule().apply(value));
        return result;
    }

    @Override
    public String getDescription()
    {
        return getKeyRule().getDescription() + ": " + getValueRule().getDescription();
    }

    @Nonnull
    public KeyValueRule required()
    {
        this.requiredField = AlwaysRequiredField.getInstance();
        return this;
    }

    @Nonnull
    public KeyValueRule requiredWhen(RequiredField requiredField)
    {
        this.requiredField = requiredField;
        return this;
    }

    public boolean isRequired(Node parent)
    {
        return requiredField != null && requiredField.isRequiredField(parent);
    }

    @Nonnull
    public KeyValueRule defaultValue(DefaultValue defaultValue)
    {
        this.defaultValue = defaultValue;
        return this;
    }

    public void applyDefault(Node parent)
    {
        if (defaultValue == null)
        {
            return;
        }

        StringValueRule keyRule = getStringKeyRule(getKeyRule());
        Node valueNode = this.defaultValue.getDefaultValue(parent);
        if (valueNode == null)
        {
            // default not applicable in fragment file
            return;
        }
        Node keyNode = new StringNodeImpl(keyRule.getValue());
        KeyValueNodeImpl newNode = new KeyValueNodeImpl(keyNode, valueNode);
        newNode.setEndPosition(DefaultPosition.emptyPosition());
        newNode.setStartPosition(DefaultPosition.emptyPosition());
        parent.addChild(newNode);
    }

    private StringValueRule getStringKeyRule(Rule keyRule)
    {

        if (keyRule instanceof AnyOfRule)
        {
            keyRule = getStringKeyRule(((AnyOfRule) keyRule).getRules().get(0));
        }

        if (!(keyRule instanceof StringValueRule))
        {
            throw new RuntimeException("Key rule " + keyRule.getClass().getSimpleName() + " does not support default values");
        }
        return (StringValueRule) keyRule;
    }

}
