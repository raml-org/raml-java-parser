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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.WarningMessageAnnotation;
import org.raml.yagi.framework.phase.Transformer;
import org.raml.yagi.framework.util.NodeSelector;

import static org.raml.v2.internal.utils.ResourcePathUtils.getUriTemplates;

public class UnusedParametersTransformer implements Transformer
{

    @Override
    public boolean matches(Node node)
    {
        return node instanceof RamlDocumentNode || node instanceof ResourceNode;
    }

    @Override
    public Node transform(Node node)
    {
        if (node instanceof RamlDocumentNode)
        {
            Node baseUriNode = NodeSelector.selectFrom("baseUri", node);
            if (baseUriNode != null)
            {
                String value = NodeSelector.selectStringValue("value", baseUriNode);
                List<String> templates = getUriTemplates(value);
                checkUriParameters("baseUriParameters", node, templates);
            }
        }
        else if (node instanceof ResourceNode)
        {
            List<String> templates = getUriTemplates(((ResourceNode) node).getRelativeUri());
            checkUriParameters("uriParameters", ((ResourceNode) node).getValue(), templates);
        }
        return node;
    }

    private void checkUriParameters(String key, Node node, List<String> templates)
    {
        Node parametersNode = NodeSelector.selectFrom(key, node);
        if (parametersNode != null)
        {
            for (Node child : parametersNode.getChildren())
            {
                String parameterName = ((SimpleTypeNode) ((KeyValueNode) child).getKey()).getLiteralValue();
                if (!templates.contains(parameterName))
                {
                    child.annotate(new WarningMessageAnnotation("Unused uri parameter '" + parameterName + "'"));
                }
            }
        }
    }
}
