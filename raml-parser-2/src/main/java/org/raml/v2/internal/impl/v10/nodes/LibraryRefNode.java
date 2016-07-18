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
package org.raml.v2.internal.impl.v10.nodes;

import org.raml.v2.internal.impl.commons.nodes.LibraryNodeProvider;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.yagi.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.nodes.AbstractReferenceNode;
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LibraryRefNode extends AbstractReferenceNode
{

    private String name;

    public LibraryRefNode(String name)
    {
        this.name = name;
    }

    public LibraryRefNode(LibraryRefNode node)
    {
        super(node);
        this.name = node.name;
    }

    @Override
    public String getRefName()
    {
        return name;
    }

    @Nullable
    @Override
    public Node resolveReference()
    {
        final Node node = selectLibraryLinkNode();
        if (node == null)
        {
            return null;
        }
        if (node instanceof LibraryLinkNode)
        {
            return ((LibraryLinkNode) node).getRefNode();
        }
        else
        {
            return null;
        }
    }

    @Nullable
    protected Node selectLibraryLinkNode()
    {
        // final Node relativeNode = getRelativeNode();
        for (Node contextNode : getContextNodes())
        {
            if (contextNode instanceof LibraryNodeProvider)
            {
                final Node libraryNode = ((LibraryNodeProvider) contextNode).getLibraryNode();
                Node node = NodeSelector.selectFrom(name, libraryNode);
                if (node != null)
                {
                    return node;
                }
            }
            else
            {
                Node node = NodeSelector.selectFrom(Raml10Grammar.USES_KEY_NAME + "/" + name, contextNode);
                if (node != null)
                {
                    return node;
                }
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new LibraryRefNode(this);
    }
}
