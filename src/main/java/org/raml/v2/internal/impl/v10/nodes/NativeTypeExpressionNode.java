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
import org.raml.v2.internal.impl.v10.type.ArrayTypeDefinition;
import org.raml.v2.internal.impl.v10.type.BooleanTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateOnlyTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateTimeOnlyTypeDefinition;
import org.raml.v2.internal.impl.v10.type.DateTimeTypeDefinition;
import org.raml.v2.internal.impl.v10.type.FileTypeDefinition;
import org.raml.v2.internal.impl.v10.type.IntegerTypeDefinition;
import org.raml.v2.internal.impl.v10.type.NullTypeDefinition;
import org.raml.v2.internal.impl.v10.type.NumberTypeDefinition;
import org.raml.v2.internal.impl.v10.type.ObjectTypeDefinition;
import org.raml.v2.internal.impl.v10.type.StringTypeDefinition;
import org.raml.v2.internal.impl.v10.type.TimeOnlyTypeDefinition;
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
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
    public TypeDefinition generateDefinition()
    {
        final TypeIds typeIds = getType(getLiteralValue());
        if (typeIds == null)
        {
            return null;
        }
        switch (typeIds)
        {
        case STRING:
            return new StringTypeDefinition();
        case NUMBER:
            return new NumberTypeDefinition();
        case INTEGER:
            return new IntegerTypeDefinition();
        case BOOLEAN:
            return new BooleanTypeDefinition();
        case DATE_ONLY:
            return new DateOnlyTypeDefinition();
        case TIME_ONLY:
            return new TimeOnlyTypeDefinition();
        case DATE_TIME_ONLY:
            return new DateTimeOnlyTypeDefinition();
        case DATE_TIME:
            return new DateTimeTypeDefinition();
        case FILE:
            return new FileTypeDefinition();
        case OBJECT:
            return new ObjectTypeDefinition();
        case ARRAY:
            return new ArrayTypeDefinition();
        case NULL:
            return new NullTypeDefinition();
        }
        return null;
    }
}
