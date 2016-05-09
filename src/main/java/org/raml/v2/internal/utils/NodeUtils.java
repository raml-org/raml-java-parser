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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.TypeNode;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYIncludeNode;

import java.util.List;

public class NodeUtils
{

    public static final int DEFAULT_COLUMN_STEP = 2;

    @Nullable
    public static Node getGrandParent(Node node)
    {
        return getAncestor(node, 2);
    }

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

    public static ObjectNode getTypesRoot(final Node node)
    {
        final Node typesRoot = getTypes(traverseToRoot(node));
        return typesRoot instanceof ObjectNode ? (ObjectNode) typesRoot : null;
    }

    public static boolean isStringNode(Node node)
    {
        return node != null && node instanceof StringNode;
    }

    public static Node getType(Node node)
    {
        return node.get("type") != null ? node.get("type") : node.get("schema");
    }

    public static Node getTypes(Node node)
    {
        return node.get("types") != null ? node.get("types") : node.get("schemas");
    }

    public static boolean isErrorResult(Node node)
    {
        return node != null && (node instanceof ErrorNode || node.findDescendantsWith(ErrorNode.class).size() > 0);
    }

    public static TypeNode getType(String typeName, Node node)
    {
        Node definitionContext = getNodeContext(node);
        if (definitionContext == null)
        {
            return null;
        }
        else if (typeName != null && typeName.contains("."))
        {
            return getTypeFromContext(typeName, definitionContext);
        }
        else if (getTypes(definitionContext) != null)
        {
            Node type = getTypes(definitionContext).get(typeName);
            return type instanceof TypeNode ? (TypeNode) type : null;
        }
        return null;
    }

    private static TypeNode getTypeFromContext(String typeName, Node definitionContext)
    {
        Node localContext = definitionContext.get("uses");
        if (localContext == null)
        {
            return null;
        }
        else
        {
            Node resolution = localContext;
            String objectName = typeName.substring(typeName.lastIndexOf(".") + 1);
            String navigationPath = typeName.substring(0, typeName.lastIndexOf("."));
            if (!navigationPath.contains("."))
            {
                return resolution != null && resolution.get(navigationPath) != null && getTypes(resolution.get(navigationPath)) != null &&
                       getTypes(resolution.get(navigationPath)).get(objectName) instanceof TypeNode ? (TypeNode) getTypes(resolution.get(navigationPath)).get(objectName) : null;
            }
            for (String path : navigationPath.split("."))
            {
                if (resolution == null)
                {
                    return null;
                }
                else
                {
                    resolution = resolution.get(path);
                }
            }
            return resolution != null && getTypes(resolution) != null && getTypes(resolution).get(objectName) instanceof TypeNode ? (TypeNode) getTypes(resolution).get(objectName) : null;
        }

    }

    private static Node getNodeContext(Node node)
    {
        if (node == null || node instanceof RamlDocumentNode)
        {
            return node;
        }
        else if (node.getSource() != null && node.getSource() instanceof SYIncludeNode)
        {
            return node;
        }
        else if (node.getParent() == null)
        {
            return node;
        }
        else
        {
            return getNodeContext(node.getParent());
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
