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
package org.raml.v2.internal.impl.v10.nodes.types.builtin;


import java.util.List;

import org.raml.v2.internal.impl.commons.nodes.PropertyNode;

public interface TypeNodeVisitor<T>
{

    T visitString(StringTypeNode stringTypeNode);

    T visitObject(ObjectTypeNode objectTypeNode);

    T visitBoolean(BooleanTypeNode booleanTypeNode);

    T visitFloat(FloatTypeNode numericTypeNode);

    T visitInteger(IntegerTypeNode numericTypeNode);

    T visitDate(DateTypeNode dateTypeNode);

    T visitExample(List<PropertyNode> properties, boolean allowsAdditionalProperties, boolean strict);

}
