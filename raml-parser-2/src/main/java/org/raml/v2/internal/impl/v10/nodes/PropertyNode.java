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

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.nodes.PropertyUtils;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;

public class PropertyNode extends KeyValueNodeImpl
{

    public PropertyNode()
    {
    }

    public PropertyNode(PropertyNode node)
    {
        super(node);
    }

    public String getName()
    {
        return PropertyUtils.getName(this);
    }

    public boolean isRequired()
    {
        return PropertyUtils.isRequired(this);
    }

    public ResolvedType getTypeDefinition()
    {
        final Node value = getValue();
        if (value instanceof TypeDeclarationNode)
        {
            return ((TypeDeclarationNode) value).getResolvedType();
        }
        else
        {
            throw new RuntimeException("Invalid value it should always be a TypeDeclarationNode but was " + value);
        }
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new PropertyNode(this);
    }

    @Override
    public String toString()
    {
        return this.getName() + ":" + org.raml.yagi.framework.util.NodeUtils.getType(this.getValue());
    }

}
