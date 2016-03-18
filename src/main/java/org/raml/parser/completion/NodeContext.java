/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.completion;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.ResourceHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class NodeContext
{

    private int parentIndentation;
    private int siblingsIndentation;
    private MappingNode mappingNode;

    public NodeContext(int parentIndentation, MappingNode mappingNode)
    {
        this.parentIndentation = parentIndentation;
        this.siblingsIndentation = -1;
        this.mappingNode = mappingNode;
    }

    public int getParentIndentation()
    {
        return parentIndentation;
    }

    public int getSiblingsIndentation()
    {
        return siblingsIndentation;
    }

    public List<String> getKeys()
    {
        List<String> keys = new ArrayList<String>();
        if (mappingNode != null)
        {
            for (NodeTuple tuple : mappingNode.getValue())
            {
                if (tuple.getKeyNode().getNodeId() == NodeId.scalar)
                {
                    String value = ((ScalarNode) tuple.getKeyNode()).getValue();
                    if (!value.equals(ResourceHandler.RESOURCE_KEY))
                    {
                        keys.add(value);
                    }
                }
                if (siblingsIndentation == -1)
                {
                    siblingsIndentation = tuple.getKeyNode().getStartMark().getColumn();
                }
            }
        }
        return keys;
    }

}
