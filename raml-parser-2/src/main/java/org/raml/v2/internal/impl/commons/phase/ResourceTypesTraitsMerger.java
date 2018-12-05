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

import org.raml.v2.internal.impl.commons.nodes.BodyNode;
import org.raml.v2.internal.impl.commons.nodes.OverridableNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.yagi.framework.grammar.rule.RegexValueRule;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.util.NodeSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

import static org.raml.yagi.framework.nodes.DefaultPosition.isDefaultNode;

public class ResourceTypesTraitsMerger
{

    private static final Logger logger = LoggerFactory.getLogger(ResourceTypesTraitsMerger.class);

    static void merge(Node baseNode, Node copyNode)
    {
        if (copyNode instanceof NullNode)
        {
            return; // nothing to do here if copyNode is null
        }
        else if (baseNode instanceof ObjectNode && copyNode instanceof ObjectNode)
        {
            merge((ObjectNode) baseNode, (ObjectNode) copyNode);
        }
        else if (baseNode instanceof ArrayNode && copyNode instanceof ArrayNode)
        {
            merge((ArrayNode) baseNode, (ArrayNode) copyNode);
        }
        else if ((baseNode instanceof NullNode) || (copyNode instanceof ErrorNode))
        {
            baseNode.replaceWith(copyNode);
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
            if (child instanceof ErrorNode)
            {
                logger.debug("Adding ErrorNode");
                baseNode.addChild(child);
                continue;
            }
            if (!(child instanceof KeyValueNode))
            {
                throw new RuntimeException("Only expecting KeyValueNode and got " + child.getClass());
            }

            String key = ((KeyValueNode) child).getKey().toString();
            if (shouldIgnoreKey((KeyValueNode) child))
            {
                logger.debug("Ignoring key '{}'", key);
                continue;
            }

            boolean optional = key.endsWith("?");
            if (optional)
            {
                key = key.substring(0, key.length() - 1);
            }
            Node node = NodeSelector.selectFrom(NodeSelector.encodePath(key), baseNode);
            Node childValue = ((KeyValueNode) child).getValue();

            if (node == null)
            {
                // if merging children of body node, media type is defined under baseNode and child is not a mime type node,
                // child gets merge with the value of mediaType node. See #498
                RegexValueRule mimeTypeRegex = new Raml10Grammar().mimeTypeRegex();
                if (baseNode.getParent() instanceof BodyNode && !mimeTypeRegex.matches(((KeyValueNode) child).getKey()))
                {
                    if (baseNode.getChildren().size() > 0 && baseNode.getChildren().get(0) instanceof KeyValueNode)
                    {
                        KeyValueNode mimeTypeNode = (KeyValueNode) baseNode.getChildren().get(0);
                        if (mimeTypeRegex.matches(mimeTypeNode.getKey()))
                        {
                            logger.debug("Overriding keys under the media type '{}'", copyNode);
                            merge(mimeTypeNode.getValue(), copyNode);
                        }
                    }
                }
                else
                {
                    logger.debug("Adding key '{}'", key);
                    baseNode.addChild(child);
                }
            }
            else if (childValue instanceof SimpleTypeNode)
            {
                if (isDefaultNode(node) && !isDefaultNode(childValue))
                {
                    logger.debug("Overriding default key '{}'", key);
                    node.getParent().setChild(1, childValue);
                }
                else if (node instanceof OverridableNode)
                {
                    logger.debug("Overriding scalar key '{}'", key);
                    node.getParent().setChild(1, childValue);
                }
                else
                {
                    logger.debug("Scalar key already exists '{}'", key);
                }
            }
            else
            {
                logger.debug("Merging values '{}' and '{}'", baseNode.getParent(), child);
                if (node instanceof SimpleTypeNode)
                {
                    merge(baseNode, childValue);
                }
                else
                {
                    merge(node, childValue);
                }
            }
        }
    }

    private static boolean shouldIgnoreKey(KeyValueNode child)
    {
        Set<String> ignoreSet = new HashSet<>();
        ignoreSet.add("usage");
        if (!(child.getParent() instanceof TypeDeclarationNode))
        {
            ignoreSet.add("type");
        }
        String key = child.getKey().toString();
        return ignoreSet.contains(key);
    }
}
