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

import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.ReferenceNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LibraryLinkNode extends AbstractRamlNode implements ReferenceNode
{
    private String path;
    private Node libraryReference;

    public LibraryLinkNode(String path)
    {
        this.path = path;
    }

    // For cloning
    protected LibraryLinkNode(LibraryLinkNode node)
    {
        super(node);
        path = node.path;
    }

    @Override
    public String getRefName()
    {
        return path;
    }

    @Nullable
    @Override
    public Node getRefNode()
    {
        return libraryReference;
    }

    public void setLibraryReference(Node libraryReference)
    {
        this.libraryReference = libraryReference;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new LibraryLinkNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Reference;
    }
}
