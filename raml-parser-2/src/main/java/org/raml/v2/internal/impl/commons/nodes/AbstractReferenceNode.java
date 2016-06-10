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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.util.NodeUtils;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractReferenceNode extends AbstractRamlNode implements ReferenceNode
{

    private Node refNode;

    public AbstractReferenceNode()
    {
    }

    public AbstractReferenceNode(AbstractReferenceNode node)
    {
        super(node);
    }

    @Nullable
    @Override
    public final Node getRefNode()
    {
        if (refNode == null)
        {
            refNode = resolveReference();
        }
        return refNode;
    }

    @Nullable
    public abstract Node resolveReference();


    public Node getRelativeNode()
    {
        if (!getChildren().isEmpty() && getChildren().get(0) instanceof ReferenceNode)
        {
            return ((ReferenceNode) getChildren().get(0)).getRefNode();
        }
        else
        {
            return NodeUtils.getContextNode(this);
        }
    }

    @Override
    public NodeType getType()
    {
        return NodeType.Reference;
    }

    public Map<String, String> getParameters()
    {
        Map<String, String> params = new HashMap<>();

        Node parametersNode = getParametersNode();
        if (parametersNode != null)
        {
            for (Node node : parametersNode.getChildren())
            {
                KeyValueNode keyValueNode = (KeyValueNode) node;
                Node value = keyValueNode.getValue();
                if (value instanceof SimpleTypeNode)
                {
                    params.put(((SimpleTypeNode) keyValueNode.getKey()).getLiteralValue(), ((SimpleTypeNode) value).getLiteralValue());
                }
                else
                {
                    throw new RuntimeException("Expecting SimpleTypeNode but got " + value.getClass());
                }
            }
        }
        return params;
    }

    public Node getParametersNode()
    {
        List<Node> children = getChildren();
        if (children.size() == 1 && children.get(0) instanceof KeyValueNode)
        {
            return ((KeyValueNode) children.get(0)).getValue();
        }
        if (children.size() == 2 && children.get(0) instanceof LibraryRefNode)
        {
            return ((KeyValueNode) children.get(1)).getValue();
        }
        return null;
    }

}
