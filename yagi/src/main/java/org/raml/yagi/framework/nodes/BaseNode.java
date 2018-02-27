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
package org.raml.yagi.framework.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.yagi.framework.util.NodeSelector;

public abstract class BaseNode implements Node
{

    private Node source;
    private Node parent;
    private Node contextNode;
    protected List<Node> children = new ArrayList<>();

    private List<NodeAnnotation> annotations = new ArrayList<>();

    public BaseNode()
    {
    }

    public BaseNode(BaseNode node)
    {
        setSource(node);
        final List<Node> children = new ArrayList<>(node.children);
        for (Node child : children)
        {
            addChild(child.copy());
        }
        annotations = new ArrayList<>(node.annotations);
    }

    @Override
    public void annotate(NodeAnnotation annotation)
    {
        this.annotations.add(annotation);
    }

    @Override
    public Collection<NodeAnnotation> annotations()
    {
        return this.annotations;
    }

    @Override
    public Node getParent()
    {
        return parent;
    }

    @Nonnull
    @Override
    public List<Node> getChildren()
    {
        return Collections.unmodifiableList(new ArrayList<>(children));
    }

    @Override
    public void addChild(Node node)
    {
        node.setParent(this);
        children.add(node);
    }

    @Override
    public void removeChild(Node node)
    {
        children.remove(node);
    }

    @Override
    public Node getRootNode()
    {
        if (getParent() != null)
            return getParent().getRootNode();

        if (contextNode != null)
            return contextNode.getRootNode();

        return this;
    }

    @Nonnull
    @Override
    public <T extends Node> List<T> findDescendantsWith(Class<T> nodeType)
    {
        final List<T> result = new ArrayList<>();
        final List<Node> children = getChildren();
        for (Node child : children)
        {
            if (nodeType.isAssignableFrom(child.getClass()))
            {
                result.add(nodeType.cast(child));
            }
            result.addAll(child.findDescendantsWith(nodeType));
        }
        return result;
    }

    @Nullable
    @Override
    public <T extends Node> T findAncestorWith(Class<T> nodeType)
    {
        Node parent = getParent();
        while (parent != null)
        {
            if (nodeType.isAssignableFrom(parent.getClass()))
            {
                return nodeType.cast(parent);
            }
            parent = parent.getParent();
        }
        return null;
    }

    @Override
    public void replaceWith(Node newNode)
    {
        if (this != newNode)
        {
            replaceTree(newNode);
            for (Node child : getChildren())
            {
                newNode.addChild(child);
            }
        }
    }

    @Override
    public void replaceTree(Node newSubTree)
    {
        if (this != newSubTree)
        {
            newSubTree.setSource(this);
            if (getParent() != null)
            {
                // If it has a parent replace it and the same idx
                int idx = getParent().getChildren().indexOf(this);
                if (idx == -1)
                {
                    // Bastard as it has a parent but is not recognized by him ;)
                    throw new RuntimeException("Trying to replace a bastard child node " + this.getClass().getSimpleName() + " on parent " + getParent() + ".");
                }
                getParent().setChild(idx, newSubTree);
            }
        }
    }

    @Override
    public void removeChildren()
    {
        for (Node child : new ArrayList<>(children))
        {
            child.setParent(null);
        }
        children.clear();
    }

    @Override
    public void setChild(int idx, Node newNode)
    {
        children.set(idx, newNode);
        newNode.setParent(this);
    }

    @Override
    public void addChild(int idx, Node newNode)
    {
        children.add(idx, newNode);
        newNode.setParent(this);
    }

    @Override
    public void setParent(Node parent)
    {
        if (this.parent != null)
        {
            this.parent.removeChild(this);
        }
        this.parent = parent;
    }

    @Override
    public void setSource(Node source)
    {
        this.source = source;
    }

    @Override
    public Node getSource()
    {
        return source;
    }

    @Override
    @Nullable
    public Node get(String selector)
    {
        return NodeSelector.selectFrom(selector, this);
    }

    @Override
    public void setContextNode(Node node)
    {
        this.contextNode = node;
    }
}
