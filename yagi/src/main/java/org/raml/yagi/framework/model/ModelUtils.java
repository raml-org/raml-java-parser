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
package org.raml.yagi.framework.model;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

public class ModelUtils
{

    private static final Set<Class<?>> WRAPPERS_PLUS_STRING = new HashSet<Class<?>>();

    static
    {
        WRAPPERS_PLUS_STRING.add(Boolean.class);
        WRAPPERS_PLUS_STRING.add(Character.class);
        WRAPPERS_PLUS_STRING.add(Byte.class);
        WRAPPERS_PLUS_STRING.add(Short.class);
        WRAPPERS_PLUS_STRING.add(Integer.class);
        WRAPPERS_PLUS_STRING.add(Long.class);
        WRAPPERS_PLUS_STRING.add(Float.class);
        WRAPPERS_PLUS_STRING.add(Double.class);
        WRAPPERS_PLUS_STRING.add(BigInteger.class);
        WRAPPERS_PLUS_STRING.add(BigDecimal.class);
        WRAPPERS_PLUS_STRING.add(String.class);
    }

    public static boolean isPrimitiveOrWrapperOrString(Class<?> type)
    {
        return type.isPrimitive() || WRAPPERS_PLUS_STRING.contains(type);
    }


    public static boolean isObject(Class<?> type)
    {
        return Object.class.equals(type);
    }

    public static Class<?> toClass(Type type)
    {
        if (type instanceof Class<?>)
        {
            return (Class<?>) type;
        }
        else if (type instanceof ParameterizedType)
        {
            return toClass(((ParameterizedType) type).getRawType());
        }
        else if (type instanceof WildcardType)
        {
            return toClass(((WildcardType) type).getUpperBounds()[0]);
        }
        else
        {
            return Object.class;
        }
    }
}
