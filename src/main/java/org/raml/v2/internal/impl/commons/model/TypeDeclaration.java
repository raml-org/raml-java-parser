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

import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.impl.commons.model.builder.ModelUtils;
import org.raml.v2.internal.impl.commons.nodes.PayloadValidationResultNode;
import org.raml.v2.internal.utils.NodeSelector;
import org.raml.v2.internal.utils.NodeUtils;
import org.raml.v2.internal.utils.NodeValidator;

public class TypeDeclaration extends Annotable
{

    private KeyValueNode node;

    public TypeDeclaration(KeyValueNode node)
    {
        this.node = node;
    }

    @Override
    public Node getNode()
    {
        return node.getValue();
    }

    public String name()
    {
        return ((StringNode) node.getKey()).getValue();
    }

    public ExampleSpec example()
    {
        Node example = NodeSelector.selectFrom("example", getNode());
        if (example != null)
        {
            return new ExampleSpec((KeyValueNode) example.getParent());
        }
        return null;
    }

    public String schemaContent()
    {
        List<String> type = type("type");
        type.addAll(type("schema"));
        if (!type.isEmpty())
        {
            return type.get(0);
        }
        return null;
    }

    public List<String> schema()
    {
        return type("schema");
    }

    public List<String> type()
    {
        return type("type");
    }

    private List<String> type(String key)
    {
        List<String> result = new ArrayList<>();
        Node type = NodeSelector.selectFrom(key, getNode());
        if (type instanceof SimpleTypeNode)
        {
            result.add(((SimpleTypeNode) type).getLiteralValue());
        }
        else if (type != null)
        {
            // TODO we can do better
            result.add(type.toString());
        }
        return result;
    }

    public List<RamlValidationResult> validate(String payload)
    {
        NodeValidator validator = new NodeValidator(NodeUtils.getResourceLoader(node));
        PayloadValidationResultNode payloadValidationResultNode = validator.validatePayload(node.getValue(), payload);
        List<RamlValidationResult> results = new ArrayList<>();
        for (ErrorNode errorNode : payloadValidationResultNode.findDescendantsWith(ErrorNode.class))
        {
            results.add(new RamlValidationResult(errorNode));
        }
        return results;
    }

    public Boolean required()
    {
        Boolean required = ModelUtils.getSimpleValue("required", getNode());
        return required == null ? true : required;
    }

    public String defaultValue()
    {
        Object defaultValue = ModelUtils.getSimpleValue("default", getNode());
        return defaultValue != null ? defaultValue.toString() : null;
    }
}
