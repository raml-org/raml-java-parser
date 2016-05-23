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
package org.raml.v2.internal.utils;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.impl.commons.nodes.ContextProviderNode;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class NodeUtils
{

    private static final int DEFAULT_COLUMN_STEP = 2;

    @Nullable
    public static Node getAncestor(Node node, int level)
    {
        int i = 1;
        Node parent = node.getParent();
        while (i < level && parent != null)
        {
            parent = parent.getParent();
            i++;
        }
        return parent;
    }

    @Nullable
    public static <T extends Node> T getAncestor(Node node, Class<T> ancestorType)
    {
        Node parent = node.getParent();
        while (parent != null && !ancestorType.isAssignableFrom(parent.getClass()))
        {
            parent = parent.getParent();
        }
        return ancestorType.cast(parent);
    }

    @Nullable
    public static <T extends Node> T getSource(Node node, Class<T> ancestorType)
    {
        Node parent = node.getSource();
        while (parent != null && !ancestorType.isAssignableFrom(parent.getClass()))
        {
            parent = parent.getSource();
        }
        return ancestorType.cast(parent);
    }

    @Nullable
    public static Node traverseToRoot(Node node)
    {
        if (node == null || node instanceof RamlDocumentNode)
        {
            return node;
        }
        else if (node.getParent() == null)
        {
            return node;
        }
        else
        {
            return traverseToRoot(node.getParent());
        }
    }

    public static boolean isStringNode(Node node)
    {
        return node != null && node instanceof StringNode;
    }

    public static Node getType(Node node)
    {
        return node.get("type") != null ? node.get("type") : node.get("schema");
    }

    public static boolean isErrorResult(Node node)
    {
        return node != null && (node instanceof ErrorNode || node.findDescendantsWith(ErrorNode.class).size() > 0);
    }


    /**
     * Returns the node that defines the scope for the specified node.
     * @param node The node
     * @return The context node for the specified node
     */
    @Nonnull
    public static Node getContextNode(Node node)
    {
        if (node.getParent() == null)
        {
            return node;
        }
        else if (node instanceof ContextProviderNode)
        {
            return ((ContextProviderNode) node).getContextNode();
        }
        else
        {
            return getContextNode(node.getParent());
        }
    }

    @Nonnull
    public static ResourceLoader getResourceLoader(Node node)
    {
        while (node != null)
        {
            if (node instanceof RamlDocumentNode)
            {
                return ((RamlDocumentNode) node).getResourceLoader();
            }
            node = node.getParent();
        }
        throw new IllegalArgumentException("node does not belong to a raml document");
    }

    public static String computeColumnForChild(Node node)
    {
        return StringUtils.repeat(" ", node.getStartPosition().getColumn() + DEFAULT_COLUMN_STEP);
    }

    @Nullable
    public static Node searchNodeAt(Node root, int location)
    {
        if (root.getEndPosition().getIndex() != location || !root.getChildren().isEmpty())
        {
            final List<Node> children = root.getChildren();
            for (Node child : children)
            {
                if (child.getEndPosition().getIndex() == location)
                {
                    if (child.getChildren().isEmpty())
                    {
                        return child;
                    }
                    else
                    {
                        return searchNodeAt(child, location);
                    }
                }
                else if (child.getEndPosition().getIndex() > location || isLastNode(child))
                {
                    if (child.getChildren().isEmpty())
                    {
                        return child;
                    }
                    else
                    {
                        return searchNodeAt(child, location);
                    }
                }
            }
            return null;
        }
        else
        {
            return root;
        }
    }

    private static boolean isLastNode(Node node)
    {
        final Node parent = node.getParent();
        if (parent == null)
        {
            return false;
        }
        List<Node> children = parent.getChildren();
        Node lastChild = children.get(children.size() - 1);
        return node.equals(lastChild);
    }

}
