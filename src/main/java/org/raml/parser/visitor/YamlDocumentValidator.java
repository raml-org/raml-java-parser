/*
 * Copyright 2016 (c) MuleSoft, Inc.
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

import static org.raml.parser.rule.ValidationMessage.NON_SCALAR_KEY_MESSAGE;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.raml.parser.visitor.TupleType.KEY;
import static org.raml.parser.visitor.TupleType.VALUE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.loader.ResourceLoaderAware;
import org.raml.parser.rule.DefaultTupleRule;
import org.raml.parser.rule.NodeRule;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.SequenceRule;
import org.raml.parser.rule.TupleRule;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.tagresolver.ContextPathAware;
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
    private List<ValidationResult> messages = new ArrayList<ValidationResult>();
    private NodeRuleFactory nodeRuleFactory;
    private ContextPath contextPath;
    private ResourceLoader resourceLoader;


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
    public boolean onMappingNodeStart(MappingNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            addMessage(createErrorResult(NON_SCALAR_KEY_MESSAGE, node));
        }
        return true;
    }

    @Override
    public void onMappingNodeEnd(MappingNode node, TupleType tupleType)
    {
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            addMessage(createErrorResult(NON_SCALAR_KEY_MESSAGE, node));
        }
        else
        {
            NodeRule<SequenceNode> peek = (NodeRule<SequenceNode>) ruleContext.peek();
            addMessages(peek.validateValue(node));
        }
        return true;
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

        if (tupleType == VALUE)
        {
            result = ((NodeRule<ScalarNode>) peek).validateValue(node);
        }
        else
        {
            result = ((TupleRule<ScalarNode, ?>) peek).validateKey(node);
        }
        addMessages(result);
    }

    private void addMessages(List<ValidationResult> result)
    {
        for (ValidationResult validationResult : result)
        {
            validationResult.setIncludeContext(contextPath);
            messages.add(validationResult);
        }
    }

    private void addMessage(ValidationResult errorResult)
    {
        addMessages(Collections.<ValidationResult>singletonList(errorResult));
    }

    @Override
    public boolean onDocumentStart(MappingNode node)
    {
        ruleContext.push(buildDocumentRule());
        return true;
    }

    @Override
    public void onDocumentEnd(MappingNode node)
    {
        NodeRule<?> pop = ruleContext.pop();

        List<ValidationResult> onRuleEnd = pop.onRuleEnd();
        addMessages(onRuleEnd);

    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        NodeRule<?> rule = ruleContext.pop();
        if (rule != null)
        {
            List<ValidationResult> onRuleEnd = rule.onRuleEnd();
            addMessages(onRuleEnd);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }
    }

    @Override
    public boolean onTupleStart(NodeTuple nodeTuple)
    {
        TupleRule<?, ?> tupleRule = (TupleRule<?, ?>) ruleContext.peek();
        if (tupleRule != null)
        {
            TupleRule<?, ?> rule = tupleRule.getRuleForTuple(nodeTuple);
            if (rule instanceof ContextPathAware)
            {
                ((ContextPathAware) rule).setContextPath(contextPath);
            }
            if (rule instanceof ResourceLoaderAware)
            {
                ((ResourceLoaderAware) rule).setResourceLoader(resourceLoader);
            }
            ruleContext.push(rule);
        }
        else
        {
            throw new IllegalStateException("Unexpected ruleContext state");
        }
        return true;
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
        addMessages(validationResults);
    }

    @Override
    public void onCustomTagStart(Tag tag, Node originalValueNode, Node node)
    {
    }

    @Override
    public void onCustomTagEnd(Tag tag, Node originalValueNode, Node node)
    {
    }

    @Override
    public void onCustomTagError(Tag tag, Node node, String message)
    {
        addMessages(Arrays.asList(createErrorResult(message, node.getStartMark(), node.getEndMark())));
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

    @Override
    public void setContextPath(ContextPath contextPath)
    {
        this.contextPath = contextPath;
    }

    @Override
    public ContextPath getContextPath()
    {
        return contextPath;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

    protected ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }
}
