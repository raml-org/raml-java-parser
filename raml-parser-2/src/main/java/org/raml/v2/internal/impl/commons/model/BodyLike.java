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

import org.raml.v2.internal.impl.commons.nodes.ExternalSchemaTypeExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.yagi.framework.model.AbstractNodeModel;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NullNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.model.NodeModel;
import org.raml.yagi.framework.util.NodeSelector;

public class BodyLike extends AbstractNodeModel<KeyValueNode>
{
    public BodyLike(KeyValueNode node)
    {
        super(node);
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

    public String schemaContent()
    {
        String schema = NodeSelector.selectStringValue("schema", getNode());

        // For an inline schema
        if (schema == null || schema.startsWith("{") || schema.startsWith("<"))
        {
            return schema;
        }

        // For an schema reference,
        Node rootRamlSchema = NodeSelector.selectFrom("/schemas/" + schema, getNode());
        if (rootRamlSchema instanceof StringNode)
        {
            return ((StringNode) rootRamlSchema).getValue();
        }

        return null;
    }

    public String schemaPath()
    {
        final Node schemaNode = NodeSelector.selectFrom("schema/*", getNode());
        final String schema;
        if (schemaNode == null)
        {
            return null; // Not a Json or XML schema
        }
        else if (schemaNode instanceof TypeExpressionNode)
        {
            schema = ((TypeExpressionNode) schemaNode).getTypeExpressionText();
        }
        else
            schema = null;
        // For an inline schema
        if (schema == null || schema.startsWith("{") || schema.startsWith("<"))
        {
            return schemaNode.getPath();
        }

        // For an schema reference,
        Node rootRamlSchema = NodeSelector.selectFrom("/schemas/" + schema, getNode());
        if (rootRamlSchema instanceof ExternalSchemaTypeExpressionNode)
        {
            return ((ExternalSchemaTypeExpressionNode) rootRamlSchema).getSchemaPath();
        }
        else
        {
            return rootRamlSchema.getPath();
        }

    }
}
