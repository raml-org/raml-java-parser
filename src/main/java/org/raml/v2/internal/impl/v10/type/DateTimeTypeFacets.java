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

import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class DateTimeTypeFacets extends BaseTypeFacets
{

    private String format;

    protected DateTimeTypeFacets copy()
    {
        return new DateTimeTypeFacets();
    }

    @Override
    public TypeFacets overwriteFacets(TypeDeclarationNode from)
    {
        final DateTimeTypeFacets result = copy();
        result.setFormat(selectStringValue("format", from));
        return overwriteFacets(result, from);
    }

    @Override
    public TypeFacets mergeFacets(TypeFacets with)
    {
        final DateTimeTypeFacets result = copy();
        if (with instanceof DateTimeTypeFacets)
        {
            result.setFormat(((DateTimeTypeFacets) with).getFormat());
        }
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeFacetsVisitor<T> visitor)
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
