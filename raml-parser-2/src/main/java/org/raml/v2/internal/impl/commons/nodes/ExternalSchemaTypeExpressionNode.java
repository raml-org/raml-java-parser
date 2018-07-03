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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;
import org.raml.v2.internal.utils.SchemaGenerator;
import org.raml.yagi.framework.nodes.AbstractStringNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.snakeyaml.SYIncludeNode;

public class ExternalSchemaTypeExpressionNode extends AbstractStringNode implements TypeExpressionNode, SimpleTypeNode<String>
{
    public ExternalSchemaTypeExpressionNode(String value)
    {
        super(value);
    }

    private ExternalSchemaTypeExpressionNode(ExternalSchemaTypeExpressionNode node)
    {
        super(node);
    }

    public String getSchemaValue()
    {
        return getValue();
    }

    public String getSchemaPath()
    {
        if (StringUtils.isNotBlank(this.getStartPosition().getIncludedResourceUri()))
        {
            return this.getStartPosition().getIncludedResourceUri();
        }
        return this.getStartPosition().getPath();
    }

    @Nullable
    public String getInternalFragment()
    {
        final Node source = org.raml.yagi.framework.util.NodeUtils.getSource(this, SYIncludeNode.class);
        if (source != null)
        {
            final String value = ((SYIncludeNode) source).getValue();
            if (value.contains("#"))
            {
                return value.substring(value.indexOf("#") + 1);
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new ExternalSchemaTypeExpressionNode(this);
    }

    public boolean isJsonSchema()
    {
        return SchemaGenerator.isJsonSchema(getSchemaValue());
    }

    public boolean isXmlSchema()
    {
        return SchemaGenerator.isXmlSchema(getSchemaValue());
    }

    @Nullable
    @Override
    public ResolvedType generateDefinition()
    {
        if (isXmlSchema())
        {
            return new XmlSchemaExternalType(this, getSchemaValue(), getSchemaPath(), getInternalFragment());
        }
        else
        {
            return new JsonSchemaExternalType(this, getSchemaValue(), getSchemaPath(), getInternalFragment());
        }

    }

    @Override
    public String getTypeExpressionText()
    {
        return getValue();
    }
}
