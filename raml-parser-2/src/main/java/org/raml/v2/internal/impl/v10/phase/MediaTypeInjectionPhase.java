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
package org.raml.v2.internal.impl.v10.phase;


import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.impl.commons.nodes.BodyNode;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.yagi.framework.nodes.BaseNode;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.ObjectNode;
import org.raml.yagi.framework.nodes.Position;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.util.NodeSelector;

public class MediaTypeInjectionPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        List<StringNode> defaultMediaTypes = getDefaultMediaTypes(tree);
        if (!defaultMediaTypes.isEmpty())
        {
            List<BodyNode> bodyNodes = tree.findDescendantsWith(BodyNode.class);
            for (BodyNode bodyNode : bodyNodes)
            {
                if (!hasExplicitMimeTypes(bodyNode))
                {
                    injectMediaTypes(bodyNode, defaultMediaTypes);
                }
            }
        }
        return tree;
    }

    private List<StringNode> getDefaultMediaTypes(Node tree)
    {
        List<StringNode> result = new ArrayList<>();
        Node mediaTypeNode = NodeSelector.selectFrom("mediaType", tree);
        if (mediaTypeNode != null)
        {
            if (mediaTypeNode instanceof StringNode)
            {
                result.add((StringNode) mediaTypeNode);
            }
            else
            {
                for (Node node : mediaTypeNode.getChildren())
                {
                    result.add((StringNode) node);
                }
            }
        }
        return result;
    }

    private void injectMediaTypes(BodyNode bodyNode, List<StringNode> defaultMediaTypes)
    {
        Node injected = new MediaTypeInjectedNode();
        for (StringNode defaultMediaType : defaultMediaTypes)
        {
            Node copy = bodyNode.getValue().copy();
            KeyValueNode keyValue = new KeyValueNodeImpl(defaultMediaType.copy(), copy);
            injected.addChild(keyValue);
        }
        bodyNode.setValue(injected);
    }

    private boolean hasExplicitMimeTypes(BodyNode bodyNode)
    {
        List<Node> children = bodyNode.getValue().getChildren();
        if (!children.isEmpty() && !children.get(0).getChildren().isEmpty())
        {
            Node key = children.get(0).getChildren().get(0);
            return new Raml10Grammar().mimeTypeRegex().matches(key);
        }
        return false;
    }

    private static class MediaTypeInjectedNode extends BaseNode implements ObjectNode
    {

        public MediaTypeInjectedNode()
        {
        }

        public MediaTypeInjectedNode(MediaTypeInjectedNode mediaTypeInjectedNode)
        {
            super(mediaTypeInjectedNode);
        }

        @Nonnull
        @Override
        public Position getStartPosition()
        {
            return getParent() != null ? getParent().getStartPosition() : DefaultPosition.emptyPosition();
        }

        @Nonnull
        @Override
        public Position getEndPosition()
        {
            return getChildren().isEmpty() ? DefaultPosition.emptyPosition() : getChildren().get(0).getEndPosition();
        }

        @Nonnull
        @Override
        public Node copy()
        {
            return new MediaTypeInjectedNode(this);
        }

        @Override
        public NodeType getType()
        {
            return NodeType.Object;
        }
    }
}
