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

import javax.annotation.Nonnull;
import java.util.Stack;

public class ErrorNode extends AbstractRamlNode
{
    private final String errorMessage;
    private String path;

    public ErrorNode(String msg)
    {
        this.errorMessage = msg;
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    public String getPath()
    {
        if (path == null)
        {
            Node previousNode = this;
            Node currentNode = previousNode.getParent();
            Stack<String> keysStack = new Stack<>();

            while (currentNode != null)
            {
                if (currentNode instanceof ArrayNode)
                {
                    // In order to get the index of the node containing the error, we compare the previous
                    // node (which is an element of the array) in the tree with all the children of the current node
                    keysStack.push(String.valueOf(currentNode.getChildren().indexOf(previousNode)));
                }
                else if (currentNode instanceof KeyValueNode)
                {
                    Node key = ((KeyValueNode) currentNode).getKey();
                    String currentKey = ((SimpleTypeNode) key).getLiteralValue().replace("/", "~1");
                    keysStack.push(currentKey);
                }
                previousNode = currentNode;
                currentNode = currentNode.getParent();
            }

            // Creating the path
            StringBuilder fullPath = new StringBuilder();

            while (!keysStack.isEmpty())
            {
                fullPath.append("/").append(keysStack.pop());
            }

            path = fullPath.length() > 0 ? fullPath.toString() : "/";
        }

        return path;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return this;
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Error;
    }

    @Override
    public String toString()
    {
        return String.format("%s [%s]", getClass().getSimpleName(), getErrorMessage());
    }
}
