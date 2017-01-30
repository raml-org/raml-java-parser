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

import javax.annotation.Nonnull;

import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;

public class ResourceNode extends KeyValueNodeImpl
{

    public ResourceNode()
    {
    }

    public ResourceNode(ResourceNode node)
    {
        super(node);
    }

    public String getRelativeUri()
    {
        Node key = getKey();
        if (key instanceof StringNode)
        {
            return ((StringNode) key).getValue();
        }
        else
        {
            throw new IllegalStateException("Key must be a string but was a " + key.getClass());
        }
    }

    public String getResourcePath()
    {
        String path = getRelativeUri();
        ResourceNode parent = getParentResourceNode();
        if (parent != null)
        {
            path = parent.getResourcePath() + path;
        }
        return path;
    }

    public ResourceNode getParentResourceNode()
    {
        Node parent = getParent();
        if (parent != null && parent.getParent() instanceof ResourceNode)
        {
            return (ResourceNode) parent.getParent();
        }
        return null;
    }

    public String getResourcePathName()
    {
        String fullPath = getRelativeUri().substring(1); // remove leading slash
        String[] path = fullPath.split("/");

        for (int i = path.length - 1; i >= 0; i--)
        {
            if (!path[i].contains("{"))
            {
                return path[i];
            }
        }

        ResourceNode parent = getParentResourceNode();
        if (parent == null)
        {
            return "";
        }
        return parent.getResourcePathName();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ResourceNode(this);
    }
}
