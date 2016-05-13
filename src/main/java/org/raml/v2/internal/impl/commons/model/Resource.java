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
package org.raml.v2.internal.impl.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.impl.commons.nodes.MethodNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.utils.NodeSelector;

public class Resource extends CommonAttributes
{

    private ResourceNode node;

    public Resource(ResourceNode node)
    {
        this.node = node;
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }

    public StringType relativeUri()
    {
        return new StringType(node.getRelativeUri());
    }

    public String resourcePath()
    {
        return node.getResourcePath();
    }

    public List<Resource> resources()
    {
        ArrayList<Resource> resultList = new ArrayList<>();
        for (Node item : node.getValue().getChildren())
        {
            if (item instanceof ResourceNode)
            {
                resultList.add(new Resource((ResourceNode) item));
            }
        }
        return resultList;
    }

    public List<Method> methods()
    {
        ArrayList<Method> resultList = new ArrayList<>();
        for (Node item : node.getValue().getChildren())
        {
            if (item instanceof MethodNode)
            {
                resultList.add(new Method((MethodNode) item));
            }
        }
        return resultList;
    }

    public List<TypeDeclaration> uriParameters()
    {
        ArrayList<TypeDeclaration> result = new ArrayList<>();
        Node uriParamsNode = NodeSelector.selectFrom("uriParameters", node.getValue());
        if (uriParamsNode != null)
        {
            for (Node child : uriParamsNode.getChildren())
            {
                result.add(new TypeDeclaration(child));
            }
        }
        return result;
    }

    public Resource parentResource()
    {
        ResourceNode parent = node.getParentResourceNode();
        return parent != null ? new Resource(parent) : null;
    }

}
