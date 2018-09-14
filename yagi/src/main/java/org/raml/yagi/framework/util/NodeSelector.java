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
package org.raml.yagi.framework.util;

import com.google.common.collect.Lists;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeSelector
{

    public static final String PARENT_EXPR = "..";
    public static final String WILDCARD_SELECTOR = "*";
    public static final String ENCODED_SLASH = "\\\\/";

    /**
     * Resolves a path in the specified node. The path uses a very simple expression system like xpath where each element is separated by /.
     * <p><b>"name"</b> -> return the value of field with key that matches the specified name. <br/>
     * <b>..</b>        -> returns the parent <br/>
     * <b>*</b>         -> wild card selector <br/>
     * <b>[number]</b>    -> returns the element at that index zero base index. The number should be equal or greater than zero</p><br/>
     *
     * @param path The path example schemas/foo
     * @param from The source where to query
     * @return The result null if no match
     */
    @Nullable
    public static Node selectFrom(String path, Node from)
    {
        if (path.startsWith("/"))
        {
            Node result = selectFrom(path.substring(1), from.getRootNode());
            Node contextNode = from.getRootNode().getContextNode();
            if (result == null && contextNode != null && !contextNode.equals(from.getRootNode()))
                return selectFrom(path, contextNode);
            return result;
        }
        else
        {
            final String[] tokens = path.split("(?<!\\\\)/"); // matches a slash not preceded by a backslash
            return selectFrom(Arrays.asList(tokens), from);
        }
    }

    @Nullable
    public static Integer selectIntValue(String path, Node from)
    {
        Number longValue = selectType(path, from, null);

        if (longValue == null)
        {
            return null;
        }

        if (longValue.intValue() > Integer.MAX_VALUE)
        {
            throw new IllegalArgumentException(longValue + " cannot be cast to int.");
        }

        return longValue.intValue();
    }

    @Nullable
    public static Number selectNumberValue(String path, Node from)
    {
        return selectType(path, from, null);
    }

    @Nullable
    public static Boolean selectBooleanValue(String path, Node from)
    {
        return selectType(path, from, null);
    }

    @Nullable
    public static String selectStringValue(String path, Node from)
    {
        return selectType(path, from, null);
    }

    public static <T> T selectType(String path, Node from, T defaultValue)
    {
        Node node = selectFrom(path, from);
        if (node != null && !(node instanceof NullNode))
        {
            return ((SimpleTypeNode<T>) node).getValue();
        }
        return defaultValue;
    }

    @Nonnull
    public static List<String> selectStringCollection(String path, Node from)
    {
        return selectCollection(path, from);
    }

    private static <T> List<T> selectCollection(String path, Node from)
    {
        ArrayList<T> selectedValues = Lists.newArrayList();
        Node selectedNode = NodeSelector.selectFrom(path, from);
        if (selectedNode != null)
        {
            if (selectedNode instanceof SimpleTypeNode)
            {
                selectedValues.add(((SimpleTypeNode<T>) selectedNode).getValue());
            }
            else if (selectedNode instanceof ArrayNode)
            {
                for (Node node : selectedNode.getChildren())
                {
                    if (node instanceof SimpleTypeNode)
                    {
                        selectedValues.add(((SimpleTypeNode<T>) node).getValue());
                    }
                }
            }
        }
        return selectedValues;
    }


    @Nullable
    private static Node selectFrom(List<String> pathTokens, Node from)
    {
        Node currentNode = from;
        for (int i = 0; i < pathTokens.size() && currentNode != null; i++)
        {
            String token = pathTokens.get(i);
            if (token.equals(WILDCARD_SELECTOR))
            {
                if (currentNode instanceof ArrayNode)
                {
                    final List<Node> children = currentNode.getChildren();
                    final List<String> remainingTokens = pathTokens.subList(i + 1, pathTokens.size());
                    for (Node child : children)
                    {
                        final Node resolve = selectFrom(remainingTokens, child);
                        if (resolve != null)
                        {
                            currentNode = resolve;
                            break;
                        }
                    }
                    break;
                }
                // else we ignore the *
            }
            else if (token.equals(PARENT_EXPR))
            {
                currentNode = currentNode.getParent();
            }
            else if (token.matches("^\\[\\d+\\]$")) // child access by index
            {
                int index = Integer.parseInt(token.substring(1, token.length() - 1));
                if (currentNode.getChildren().size() > index)
                {
                    currentNode = currentNode.getChildren().get(index);
                }
                else
                {
                    currentNode = null;
                }
            }
            else if (currentNode instanceof ObjectNode)
            {
                currentNode = findValueWithName(currentNode, token);
            }
            else if (currentNode instanceof ArrayNode)
            {
                final int index = Integer.parseInt(token);
                currentNode = findElementAtIndex(currentNode, index);
            }
            else
            {
                currentNode = null;
            }
        }

        return currentNode;
    }

    @Nullable
    private static Node findElementAtIndex(final Node currentNode, int index)
    {
        Node result = null;
        final List<Node> children = currentNode.getChildren();
        if (children.size() > index)
        {
            result = children.get(index);
        }
        return result;
    }

    @Nullable
    private static Node findValueWithName(final Node currentNode, String token)
    {
        Node result = null;
        final List<Node> children = currentNode.getChildren();
        for (Node child : children)
        {
            if (child instanceof KeyValueNode)
            {
                final Node key = ((KeyValueNode) child).getKey();
                if (key instanceof SimpleTypeNode)
                {
                    if (token.equals(encodePath(String.valueOf(((SimpleTypeNode) key).getValue()))))
                    {
                        result = ((KeyValueNode) child).getValue();
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static String encodePath(final String path)
    {
        return path.replaceAll("/", ENCODED_SLASH);
    }


}
