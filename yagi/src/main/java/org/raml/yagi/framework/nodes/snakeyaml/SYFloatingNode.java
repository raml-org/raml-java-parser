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
package org.raml.yagi.framework.nodes.snakeyaml;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.FloatingNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.math.BigDecimal;

import javax.annotation.Nonnull;

public class SYFloatingNode extends SYBaseRamlNode implements FloatingNode
{
    public SYFloatingNode(SYFloatingNode node)
    {
        super(node);
    }

    public SYFloatingNode(ScalarNode yamlNode, String resourcePath, ResourceLoader resourceLoader)
    {
        super(yamlNode, resourcePath, resourceLoader);
    }

    @Override
    public BigDecimal getValue()
    {
        final String value = ((ScalarNode) getYamlNode()).getValue();
        try
        {
            return new BigDecimal(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    @Override
    public String toString()
    {
        BigDecimal value = getValue();
        return String.valueOf(value != null ? value : ((ScalarNode) getYamlNode()).getValue());
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new SYFloatingNode(this);
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Float;
    }
}
