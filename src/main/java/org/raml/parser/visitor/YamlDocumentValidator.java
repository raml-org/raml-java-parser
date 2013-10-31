/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.visitor;

import static org.raml.parser.tagresolver.IncludeResolver.INCLUDE_TAG;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.raml.parser.rule.DefaultTupleRule;
import org.raml.parser.rule.NodeRule;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.SequenceRule;
import org.raml.parser.rule.TupleRule;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlDocumentValidator implements YamlValidator
{

    private Class<?> documentClass;
    private Stack<NodeRule<?>> ruleContext = new Stack<NodeRule<?>>();
    private Stack<String> includeContext = new Stack<String>();
    private List<ValidationResult> messages = new ArrayList<ValidationResult>();
    private NodeRuleFactory nodeRuleFactory;


    protected YamlDocumentValidator(Class<?> documentClass)
    {
        this(documentClass, new NodeRuleFactory());
    }

    protected YamlDocumentValidator(Class<?> documentClass, NodeRuleFactory nodeRuleFactory)
    {
        this.documentClass = documentClass;
        this.nodeRuleFactory = nodeRuleFactory;
    }

    protected Stack<NodeRule<?>> getRuleContext()
    {
        return ruleContext;
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {

    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode)
    {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        NodeRule peek = ruleContext.peek();

        switch (tupleType)
        {
            case VALUE:
                result = ((NodeRule<SequenceNode>) peek).validateValue(node);
                break;
        }
        addMessagesIfRequired(node, result);
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {
        List<ValidationResult> result;
        NodeRule<?> peek = ruleContext.peek();

        switch (tupleType)
        {
            case VALUE:
                result = ((NodeRule<ScalarNode>) peek).validateValue(node);
                break;

            default:
                result = ((TupleRule<ScalarNode, ?>) peek).validateKey(node);
                break;
        }
        addMessagesIfRequired(node, result);
    }

    private void addMessagesIfRequired(Node node, List<ValidationResult> result)
    {
        for (ValidationResult validationResult : result)
        {
            validationResult.setIncludeName(includeContext.empty() ? null : includeContext.peek());
            messages.add(validationResult);
        }
    }

    @Override
    public void onDocumentStart(MappingNode node)
    {
        ruleContext.push(buildDocumentRule());
    }

    @Override
    public void onDocumentEnd(MappingNode node)
    {
        NodeRule<?> pop = ruleContext.pop();

        List<ValidationResult> onRuleEnd = pop.onRuleEnd();
        addMessagesIfRequired(node, onRuleEnd);

    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        NodeRule<?> rule = ruleContext.pop();
        if (rule != null)
        {
            List<ValidationResult> onRuleEnd = rule.onRuleEnd();
            addMessagesIfRequired(nodeTuple.getKeyNode(), onRuleEnd);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }
    }

    @Override
    public void onTupleStart(NodeTuple nodeTuple)
    {

        TupleRule<?, ?> tupleRule = (TupleRule<?, ?>) ruleContext.peek();
        if (tupleRule != null)
        {
            TupleRule<?, ?> rule = tupleRule.getRuleForTuple(nodeTuple);
            ruleContext.push(rule);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }

    }

    @Override
    public void onSequenceElementStart(Node sequenceNode)
    {
        NodeRule peek = ruleContext.peek();
        if (!(peek instanceof SequenceRule))
        {
            ruleContext.push(peek);
        }
        else
        {
            ruleContext.push(((SequenceRule) peek).getItemRule());
        }
    }

    @Override
    public void onSequenceElementEnd(Node sequenceNode)
    {
        NodeRule<?> rule = ruleContext.pop();
        List<ValidationResult> validationResults = rule.onRuleEnd();
        addMessagesIfRequired(sequenceNode, validationResults);
    }

    @Override
    public void onCustomTagStart(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
    {
        if (INCLUDE_TAG.equals(tag))
        {
            includeContext.push(((ScalarNode) originalValueNode).getValue());
        }
    }

    @Override
    public void onCustomTagEnd(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
    {
        if (INCLUDE_TAG.equals(tag))
        {
            String actualInclude = includeContext.pop();
            String expectedInclude = ((ScalarNode) originalValueNode).getValue();
            if (!actualInclude.equals(expectedInclude))
            {
                throw new IllegalStateException(String.format("actualInclude zombie! (actual: %s, expected: %s)", actualInclude, expectedInclude));
            }
        }
    }

    @Override
    public void onCustomTagError(Tag tag, Node node, String message)
    {
        addMessagesIfRequired(node, Arrays.asList(ValidationResult.createErrorResult(message, node.getStartMark(), node.getEndMark())));
    }


    private DefaultTupleRule<Node, MappingNode> buildDocumentRule()
    {

        return nodeRuleFactory.createDocumentRule(documentClass);
    }


    @Override
    public List<ValidationResult> getMessages()
    {
        return messages;
    }
}
