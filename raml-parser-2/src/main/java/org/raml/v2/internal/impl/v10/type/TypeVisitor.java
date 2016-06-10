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


import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;

public interface TypeVisitor<T>
{

    T visitString(StringResolvedType stringTypeDefinition);

    T visitObject(ObjectResolvedType objectTypeDefinition);

    T visitBoolean(BooleanResolvedType booleanTypeDefinition);

    T visitInteger(IntegerResolvedType integerTypeDefinition);

    T visitNumber(NumberResolvedType numberTypeDefinition);

    T visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition);

    T visitDate(DateOnlyResolvedType dateOnlyTypeDefinition);

    T visitDateTime(DateTimeResolvedType dateTimeTypeDefinition);

    T visitFile(FileResolvedType fileTypeDefinition);

    T visitNull(NullResolvedType nullTypeDefinition);

    T visitArray(ArrayResolvedType arrayTypeDefinition);

    T visitUnion(UnionResolvedType unionTypeDefinition);

    T visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition);

    T visitJson(JsonSchemaExternalType jsonTypeDefinition);

    T visitXml(XmlSchemaExternalType xmlTypeDefinition);

    T visitAny(AnyResolvedType anyResolvedType);
}
