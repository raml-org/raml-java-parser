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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.type.AnyResolvedType;
import org.raml.v2.internal.impl.v10.type.ArrayResolvedType;
import org.raml.v2.internal.impl.v10.type.BooleanResolvedType;
import org.raml.v2.internal.impl.v10.type.DateOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.DateTimeOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.DateTimeResolvedType;
import org.raml.v2.internal.impl.v10.type.FileResolvedType;
import org.raml.v2.internal.impl.v10.type.IntegerResolvedType;
import org.raml.v2.internal.impl.v10.type.NullResolvedType;
import org.raml.v2.internal.impl.v10.type.NumberResolvedType;
import org.raml.v2.internal.impl.v10.type.ObjectResolvedType;
import org.raml.v2.internal.impl.v10.type.StringResolvedType;
import org.raml.v2.internal.impl.v10.type.TimeOnlyResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeId;
import org.raml.yagi.framework.nodes.AbstractStringNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

public class NativeTypeExpressionNode extends AbstractStringNode implements TypeExpressionNode, SimpleTypeNode<String>
{

    protected NativeTypeExpressionNode(NativeTypeExpressionNode node)
    {
        super(node);
    }

    public NativeTypeExpressionNode()
    {
        super(TypeId.STRING.getType());
    }

    public NativeTypeExpressionNode(String value)
    {
        super(value);
    }

    public static boolean isNativeType(String type)
    {
        for (TypeId builtInScalarType : TypeId.values())
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

    public static TypeId getType(String type)
    {
        if ("date".equals(type))
        {
            type = "date-only"; // bridge 0.8 and 1.0 date type
        }
        for (TypeId builtInScalarType : TypeId.values())
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
    public ResolvedType generateDefinition()
    {
        final TypeId typeId = getType(getLiteralValue());
        if (typeId == null)
        {
            return null;
        }
        switch (typeId)
        {
        case STRING:
            return new StringResolvedType(this);
        case NUMBER:
            return new NumberResolvedType(this);
        case INTEGER:
            return new IntegerResolvedType(this);
        case BOOLEAN:
            return new BooleanResolvedType(this);
        case DATE_ONLY:
            return new DateOnlyResolvedType(this);
        case TIME_ONLY:
            return new TimeOnlyResolvedType(this);
        case DATE_TIME_ONLY:
            return new DateTimeOnlyResolvedType(this);
        case DATE_TIME:
            return new DateTimeResolvedType(this);
        case FILE:
            return new FileResolvedType(this);
        case OBJECT:
            return new ObjectResolvedType(this);
        case ARRAY:
            return new ArrayResolvedType(this);
        case NULL:
            return new NullResolvedType(this);
        case ANY:
            return new AnyResolvedType(this);
        }
        return new AnyResolvedType(this);
    }

    @Override
    public String getTypeExpressionText()
    {
        return getValue();
    }
}
