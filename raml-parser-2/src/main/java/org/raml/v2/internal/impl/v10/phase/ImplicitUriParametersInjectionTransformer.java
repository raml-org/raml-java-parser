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

import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.yagi.framework.phase.Transformer;
import org.raml.yagi.framework.util.NodeSelector;

import java.util.List;

import static org.raml.v2.internal.utils.ResourcePathUtils.getUriTemplates;

public class ImplicitUriParametersInjectionTransformer implements Transformer
{
    final private static String URI_PARAMETERS = "uriParameters";

    @Override
    public boolean matches(Node node)
    {
        return node instanceof ResourceNode;
    }

    @Override
    public Node transform(Node node)
    {
        ResourceNode resourceNode = (ResourceNode) node;
        List<String> templates = getUriTemplates(resourceNode.getRelativeUri());
        // version should be defined at root level
        templates.remove("version");
        injectImplicitUriParameters(resourceNode.getValue(), templates);

        return node;
    }

    private void injectImplicitUriParameters(Node node, List<String> templates)
    {
        Node parametersNode = NodeSelector.selectFrom(URI_PARAMETERS, node);
        if (parametersNode != null)
        {
            for (Node child : parametersNode.getChildren())
            {
                String parameterName = ((SimpleTypeNode) ((KeyValueNode) child).getKey()).getLiteralValue();
                templates.remove(parameterName.endsWith("?") ? parameterName.substring(0, parameterName.length() - 1) : parameterName);
            }
            if (!templates.isEmpty())
            {
                addUriParameters(templates, parametersNode);
            }
        }
        else if (!templates.isEmpty())
        {
            StringNodeImpl uriParameters = new StringNodeImpl(URI_PARAMETERS);
            ObjectNodeImpl objectNode = new ObjectNodeImpl();

            addUriParameters(templates, objectNode);
            node.addChild(new KeyValueNodeImpl(uriParameters, objectNode));
        }

    }

    private void addUriParameters(List<String> templates, Node objectNode)
    {
        for (String uriParameter : templates)
        {
            PropertyNode propertyNode = new PropertyNode();
            propertyNode.addChild(new StringNodeImpl(uriParameter));
            propertyNode.addChild(new NativeTypeExpressionNode("string"));
            objectNode.addChild(propertyNode);
        }
    }
}
