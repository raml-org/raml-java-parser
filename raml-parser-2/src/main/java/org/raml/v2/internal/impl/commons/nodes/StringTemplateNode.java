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
package org.raml.v2.internal.impl.commons.nodes;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.AbstractStringNode;
import org.raml.yagi.framework.nodes.ExecutableNode;
import org.raml.yagi.framework.nodes.ExecutionContext;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;

public class StringTemplateNode extends AbstractStringNode implements ExecutableNode
{

    public StringTemplateNode(String value)
    {
        super(value);
    }

    public StringTemplateNode(StringTemplateNode node)
    {
        super(node);
    }

    @Override
    public void addChild(Node node)
    {
        if (!(node instanceof StringNode))
        {
            throw new IllegalArgumentException("Only String nodes are valid as children");
        }
        super.addChild(node);
    }

    public Node execute(ExecutionContext context)
    {
        final List<Node> executedNodes = executeNodes(context, getChildren());
        return resolveTemplate(context, executedNodes);
    }

    private Node resolveTemplate(ExecutionContext context, List<Node> executedNodes)
    {
        if (executedNodes.size() == 1 && !(executedNodes.get(0) instanceof StringNode))
        {
            return executedNodes.get(0);
        }
        else
        {
            final StringBuilder content = new StringBuilder();
            Node referenceContext = null;
            for (Node executedNode : executedNodes)
            {
                if (executedNode instanceof SimpleTypeNode)
                {
                    content.append(((SimpleTypeNode) executedNode).getLiteralValue());
                    if (referenceContext == null && executedNode instanceof ContextAwareStringNodeImpl)
                    {
                        // use reference context node from parameter node
                        referenceContext = ((ContextAwareNode) executedNode).getReferenceContext();
                    }
                }
                else
                {
                    return ErrorNodeFactory.createInvalidType(executedNode, NodeType.String);
                }
            }
            if (referenceContext == null)
            {
                // no context defined in parameter node, use context of current node
                referenceContext = context.getContextNode();
            }
            return new ContextAwareStringNodeImpl(content.toString(), referenceContext);
        }
    }

    private List<Node> executeNodes(ExecutionContext context, List<Node> children)
    {
        List<Node> executedNodes = new ArrayList<>();
        for (Node child : children)
        {
            if (child instanceof ExecutableNode)
            {
                executedNodes.add(((ExecutableNode) child).execute(context));
            }
            else
            {
                executedNodes.add(child);
            }
        }
        return executedNodes;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new StringTemplateNode(this);
    }
}
