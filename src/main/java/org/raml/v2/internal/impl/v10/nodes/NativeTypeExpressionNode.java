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
package org.raml.v2.internal.impl.v10.nodes;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.ArrayTypeFacets;
import org.raml.v2.internal.impl.v10.type.BooleanTypeFacets;
import org.raml.v2.internal.impl.v10.type.DateOnlyTypeFacets;
import org.raml.v2.internal.impl.v10.type.DateTimeOnlyTypeFacets;
import org.raml.v2.internal.impl.v10.type.DateTimeTypeFacets;
import org.raml.v2.internal.impl.v10.type.FileTypeFacets;
import org.raml.v2.internal.impl.v10.type.IntegerTypeFacets;
import org.raml.v2.internal.impl.v10.type.NullTypeFacets;
import org.raml.v2.internal.impl.v10.type.NumberTypeFacets;
import org.raml.v2.internal.impl.v10.type.ObjectTypeFacets;
import org.raml.v2.internal.impl.v10.type.StringTypeFacets;
import org.raml.v2.internal.impl.v10.type.TimeOnlyTypeFacets;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.v10.type.TypeIds;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NativeTypeExpressionNode extends StringNodeImpl implements TypeExpressionNode
{

    public NativeTypeExpressionNode(StringNodeImpl node)
    {
        super(node);
    }

    public NativeTypeExpressionNode()
    {
        super(TypeIds.STRING.getType());
    }

    public NativeTypeExpressionNode(String value)
    {
        super(value);
    }

    public static boolean isNativeType(String type)
    {
        for (TypeIds builtInScalarType : TypeIds.values())
        {
            if (builtInScalarType.getType().equals(type))
            {
                return true;
            }
        }
        return false;
    }


    @Nonnull
    @Override
    public Node copy()
    {
        return new NativeTypeExpressionNode(this);
    }

    public static TypeIds getType(String type)
    {
        for (TypeIds builtInScalarType : TypeIds.values())
        {
            if (builtInScalarType.getType().equals(type))
            {
                return builtInScalarType;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public TypeFacets generateDefinition()
    {
        final TypeIds typeIds = getType(getLiteralValue());
        if (typeIds == null)
        {
            return null;
        }
        switch (typeIds)
        {
        case STRING:
            return new StringTypeFacets();
        case NUMBER:
            return new NumberTypeFacets();
        case INTEGER:
            return new IntegerTypeFacets();
        case BOOLEAN:
            return new BooleanTypeFacets();
        case DATE_ONLY:
            return new DateOnlyTypeFacets();
        case TIME_ONLY:
            return new TimeOnlyTypeFacets();
        case DATE_TIME_ONLY:
            return new DateTimeOnlyTypeFacets();
        case DATE_TIME:
            return new DateTimeTypeFacets();
        case FILE:
            return new FileTypeFacets();
        case OBJECT:
            return new ObjectTypeFacets();
        case ARRAY:
            return new ArrayTypeFacets();
        case NULL:
            return new NullTypeFacets();
        }
        return null;
    }
}
