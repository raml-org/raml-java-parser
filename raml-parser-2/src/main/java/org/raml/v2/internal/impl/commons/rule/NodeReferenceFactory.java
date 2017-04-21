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
package org.raml.v2.internal.impl.commons.rule;

import org.raml.v2.internal.impl.commons.nodes.AbstractReferenceNode;
import org.raml.v2.internal.impl.commons.nodes.ContextAwareNode;
import org.raml.v2.internal.impl.commons.nodes.ContextAwareStringNodeImpl;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.yagi.framework.grammar.rule.ClassNodeFactory;
import org.raml.yagi.framework.grammar.rule.NodeFactory;
import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;

import javax.annotation.Nonnull;

public class NodeReferenceFactory implements NodeFactory
{

    private NodeFactory defaultFactory;

    public NodeReferenceFactory(Class<? extends Node> referenceClassNode)
    {
        defaultFactory = new ClassNodeFactory(referenceClassNode);
    }

    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        final String value = (String) args[0];
        return parse(currentNode, value, 0);
    }

    public Node parse(Node currentNode, String value, int startLocation)
    {

        final String[] parts = value.split("\\.");
        if (parts.length > 2)
        {
            return RamlErrorNodeFactory.createInvalidLibraryChaining(value);
        }
        Node result = null;
        Node parent = null;
        int currentShift = value.length();
        for (int i = parts.length - 1; i >= 0; i--)
        {
            String part = parts[i];
            currentShift -= part.length();
            final Position endPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift + value.length());
            final Position startPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift);
            if (parent == null)
            {
                parent = defaultFactory.create(currentNode, part);
                if (parent instanceof AbstractRamlNode)
                {
                    ((AbstractRamlNode) parent).setStartPosition(startPosition);
                    ((AbstractRamlNode) parent).setEndPosition(endPosition);
                }
                result = parent;
            }
            else
            {
                final LibraryRefNode libraryRefNode = new LibraryRefNode(part);
                libraryRefNode.setStartPosition(startPosition);
                libraryRefNode.setEndPosition(endPosition);
                parent.addChild(libraryRefNode);
                parent = libraryRefNode;
                // The 1 is from the dot
                currentShift -= 1;
            }
            if (i == 0)
            {
                if (currentNode instanceof ContextAwareStringNodeImpl && parent instanceof AbstractReferenceNode)
                {
                    ((AbstractReferenceNode) parent).setContextNode(((ContextAwareNode) currentNode).getReferenceContext());
                }
            }
        }
        return result;
    }
}
