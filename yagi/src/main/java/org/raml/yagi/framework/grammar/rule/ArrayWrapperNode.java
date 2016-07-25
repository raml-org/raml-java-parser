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
import org.raml.yagi.framework.nodes.BaseNode;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.Position;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class ArrayWrapperNode extends BaseNode implements ArrayNode
{

    private Node wrapped;

    public ArrayWrapperNode(Node child)
    {
        this.wrapped = child;
        this.wrapped.setParent(this);
    }


    @Override
    public boolean isJsonStyle()
    {
        return false;
    }

    @Override
    public void addChild(Node node)
    {
        // This is a wrapper so we add the child nodes to the wrapper valued
        wrapped.addChild(node);
    }

    @Override
    public void setChild(int idx, Node newNode)
    {
        if (newNode instanceof ErrorNode)
        {
            wrapped = newNode;
        }
        else
        {
            wrapped.setChild(idx, newNode);
        }
    }

    @Override
    public void addChild(int idx, Node newNode)
    {
        // This is a wrapper so we add the child nodes to the wrapper valued
        wrapped.addChild(idx, newNode);
    }

    @Override
    public void removeChild(Node node)
    {
        // This is a wrapper so we add the child nodes to the wrapper valued
        wrapped.removeChild(node);
    }

    @Nonnull
    @Override
    public List<Node> getChildren()
    {
        return Collections.singletonList(wrapped);
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ArrayWrapperNode(wrapped);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Array;
    }
}
