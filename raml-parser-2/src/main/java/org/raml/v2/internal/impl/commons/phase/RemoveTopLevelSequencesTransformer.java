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
package org.raml.v2.internal.impl.commons.phase;


import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.Transformer;
import org.raml.yagi.framework.util.NodeSelector;

public class RemoveTopLevelSequencesTransformer implements Transformer
{

    @Override
    public boolean matches(Node node)
    {
        return node.getParent() == null;
    }

    @Override
    public Node transform(Node node)
    {
        String[] paths = {"schemas", "resourceTypes", "traits", "securitySchemes"};
        for (String path : paths)
        {
            removeSequenceForPath(node, path);
        }
        return node;
    }

    private void removeSequenceForPath(Node node, String path)
    {
        Node container = NodeSelector.selectFrom(path, node);
        if (container instanceof ArrayNode)
        {
            if (!container.getChildren().isEmpty())
            {
                Node uncle = container.getChildren().get(0);
                for (int i = 1; i < container.getChildren().size(); i++)
                {
                    Node sibling = container.getChildren().get(i);
                    for (Node nephew : sibling.getChildren())
                    {
                        uncle.addChild(nephew);
                    }
                }
                container.replaceTree(uncle);
            }
        }
    }
}
