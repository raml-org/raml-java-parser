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
/*
 *
 */
package org.raml.v2.internal.impl.commons.phase;

import org.raml.v2.internal.impl.commons.nodes.ExtendsNode;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.utils.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExtensionsMerger
{

    private static final Logger logger = LoggerFactory.getLogger(ExtensionsMerger.class);

    public static void merge(Node baseNode, Node copyNode)
    {
        if (baseNode instanceof ObjectNode && copyNode instanceof ObjectNode)
        {
            merge((ObjectNode) baseNode, (ObjectNode) copyNode);
        }
        else if (baseNode instanceof ArrayNode && copyNode instanceof ArrayNode)
        {
            merge((ArrayNode) baseNode, (ArrayNode) copyNode);
        }
        else
        {
            throw new RuntimeException(String.format("Merging not supported for nodes of type %s and %s",
                    baseNode.getClass().getSimpleName(), copyNode.getClass().getSimpleName()));
        }
    }

    static void merge(ArrayNode baseNode, ArrayNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            baseNode.addChild(child);
        }
    }

    static void merge(ObjectNode baseNode, ObjectNode copyNode)
    {
        for (Node child : copyNode.getChildren())
        {
            if (!(child instanceof KeyValueNode))
            {
                throw new RuntimeException("only expecting KeyValueNode");
            }

            Node keyNode = ((KeyValueNode) child).getKey();
            String key = keyNode.toString();
            if (shouldIgnoreNode(child))
            {
                logger.info("Ignoring key '{}'", key);
                continue;
            }

            Node node = NodeSelector.selectFrom(key, baseNode);
            if (node == null)
            {
                logger.info("Adding key '{}'", key);
                baseNode.addChild(child);
            }
            else if (((KeyValueNode) child).getValue() instanceof SimpleTypeNode)
            {
                logger.info("Replacing existing scalar key '{}'", key);
                node.replaceWith(((KeyValueNode) child).getValue());
            }
            else
            {
                logger.info("Merging values '{}' and '{}'", node.getParent(), child);
                merge(node, ((KeyValueNode) child).getValue());
            }
        }
    }

    private static boolean shouldIgnoreNode(Node node)
    {
        if (node instanceof ExtendsNode)
        {
            return true;
        }
        if (isUsageNode(node))
        {
            return true;
        }
        return false;
    }

    private static boolean isUsageNode(Node node)
    {
        Node keyNode = ((KeyValueNode) node).getKey();
        String key = keyNode.toString();
        return "usage".equals(key) && (node.getParent() instanceof RamlDocumentNode);
    }

}
