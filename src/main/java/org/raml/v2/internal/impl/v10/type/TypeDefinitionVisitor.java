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


import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeDefinition;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeDefinition;

public interface TypeDefinitionVisitor<T>
{

    T visitString(StringTypeDefinition stringTypeDefinition);

    T visitObject(ObjectTypeDefinition objectTypeDefinition);

    T visitBoolean(BooleanTypeDefinition booleanTypeDefinition);

    T visitInteger(IntegerTypeDefinition integerTypeDefinition);

    T visitNumber(NumberTypeDefinition numberTypeDefinition);

    T visitDateTimeOnly(DateTimeOnlyTypeDefinition dateTimeOnlyTypeDefinition);

    T visitDate(DateOnlyTypeDefinition dateOnlyTypeDefinition);

    T visitDateTime(DateTimeTypeDefinition dateTimeTypeDefinition);

    T visitFile(FileTypeDefinition fileTypeDefinition);

    T visitNull(NullTypeDefinition nullTypeDefinition);

    T visitArray(ArrayTypeDefinition arrayTypeDefinition);

    T visitUnion(UnionTypeDefinition unionTypeDefinition);

    T visitTimeOnly(TimeOnlyTypeDefinition timeOnlyTypeDefinition);

    T visitJson(JsonSchemaTypeDefinition jsonTypeDefinition);

    T visitXml(XmlSchemaTypeDefinition xmlTypeDefinition);
}
