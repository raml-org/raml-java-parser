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
package org.raml.v2.internal.impl.v10.grammar;

public enum BuiltInScalarType
{
    STRING("string"), NUMBER("number"), INTEGER("integer"), BOOLEAN("boolean"), DATE_ONLY("date-only"), TIME_ONLY("time-only"), DATE_TIME_ONLY("datetime-only"), DATE_TIME("datetime"), FILE("file");

    private final String type;

    BuiltInScalarType(String type)
    {
        this.type = type;
    }

    public static boolean isBuiltInScalarType(String type)
    {
        for (BuiltInScalarType builtInScalarType : values())
        {
            if (builtInScalarType.type.equals(type))
            {
                return true;
            }
        }
        return false;
    }

    public String getType()
    {
        return this.type;
    }
}
