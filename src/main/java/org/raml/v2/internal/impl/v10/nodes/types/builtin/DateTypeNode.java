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
package org.raml.v2.internal.impl.v10.nodes.types.builtin;

import static org.raml.v2.internal.impl.commons.model.builder.ModelUtils.getStringValue;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.AbstractRamlNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.raml.v2.internal.framework.nodes.ObjectNode;

public class DateTypeNode extends AbstractRamlNode implements ObjectNode, TypeNode
{

    private final static String DEFAULT_RFC = "rfc3339";

    public DateTypeNode()
    {
    }

    private DateTypeNode(DateTypeNode node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new DateTypeNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Object;
    }

    @Override
    public <T> T visit(TypeNodeVisitor<T> visitor)
    {
        return visitor.visitDate(this);
    }

    public String getDateType()
    {
        return getStringValue("type", this);
    }

    public String getRFC()
    {
        String format = getStringValue("format", this);
        return format != null ? format : DEFAULT_RFC;
    }

    public String getValue()
    {
        return null;
    }
}
