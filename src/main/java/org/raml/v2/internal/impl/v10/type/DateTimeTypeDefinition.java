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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.impl.commons.type.BaseTypeDefinition;
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class DateTimeTypeDefinition extends BaseTypeDefinition
{

    private String format;

    protected DateTimeTypeDefinition copy()
    {
        return new DateTimeTypeDefinition();
    }

    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        final DateTimeTypeDefinition result = copy();
        result.setFormat(selectStringValue("format", from));
        return result;
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        final DateTimeTypeDefinition result = copy();
        if (with instanceof DateTimeTypeDefinition)
        {
            result.setFormat(((DateTimeTypeDefinition) with).getFormat());
        }
        return result;
    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
    {
        return visitor.visitDateTime(this);
    }

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        if (format != null)
        {
            this.format = format;
        }
    }
}
