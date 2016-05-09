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
package org.raml.v2.internal.impl.v10.nodes.types;

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.nodes.PropertyNode;
import org.raml.v2.internal.framework.nodes.BaseNode;
import org.raml.v2.internal.framework.nodes.DefaultPosition;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.Position;

public class InheritedPropertiesInjectedNode extends BaseNode implements ObjectNode
{

    public InheritedPropertiesInjectedNode()
    {
    }

    public InheritedPropertiesInjectedNode(InheritedPropertiesInjectedNode inheritedPropertiesInjectedNode)
    {
        super(inheritedPropertiesInjectedNode);
    }

    public List<PropertyNode> getProperties()
    {
        return this.findDescendantsWith(PropertyNode.class);
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new InheritedPropertiesInjectedNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

}
