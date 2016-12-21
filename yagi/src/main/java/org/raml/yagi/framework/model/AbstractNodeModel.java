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
package org.raml.yagi.framework.model;

import com.google.common.base.Preconditions;
import org.raml.yagi.framework.nodes.Node;

public abstract class AbstractNodeModel<T extends Node> implements NodeModel
{
    protected final T node;

    public AbstractNodeModel(T node)
    {
        Preconditions.checkNotNull(node);
        this.node = node;
    }

    @Override
    public int hashCode()
    {
        return node.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof NodeModel))
        {
            return false;
        }
        final NodeModel nodeModel = (NodeModel) obj;
        return getNode().equals(nodeModel.getNode());
    }
}
