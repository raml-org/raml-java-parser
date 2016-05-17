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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import org.raml.v2.internal.framework.nodes.FloatingNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.NodeType;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

public class SYFloatingNode extends SYBaseRamlNode implements FloatingNode
{
    public SYFloatingNode(SYFloatingNode node, String resourcePath)
    {
        super(node, resourcePath);
    }

    public SYFloatingNode(ScalarNode yamlNode, String resourcePath)
    {
        super(yamlNode, resourcePath);
    }

    @Override
    public BigDecimal getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        return new BigDecimal(value);
    }

    @Override
    public String toString()
    {
        return String.valueOf(getValue());
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYFloatingNode(this, getResourcePath());
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Float;
    }
}
