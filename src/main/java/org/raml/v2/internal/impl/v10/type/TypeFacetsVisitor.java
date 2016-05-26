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


import org.raml.v2.internal.impl.commons.type.JsonSchemaTypeFacets;
import org.raml.v2.internal.impl.commons.type.XmlSchemaTypeFacets;

public interface TypeFacetsVisitor<T>
{

    T visitString(StringTypeFacets stringTypeDefinition);

    T visitObject(ObjectTypeFacets objectTypeDefinition);

    T visitBoolean(BooleanTypeFacets booleanTypeDefinition);

    T visitInteger(IntegerTypeFacets integerTypeDefinition);

    T visitNumber(NumberTypeFacets numberTypeDefinition);

    T visitDateTimeOnly(DateTimeOnlyTypeFacets dateTimeOnlyTypeDefinition);

    T visitDate(DateOnlyTypeFacets dateOnlyTypeDefinition);

    T visitDateTime(DateTimeTypeFacets dateTimeTypeDefinition);

    T visitFile(FileTypeFacets fileTypeDefinition);

    T visitNull(NullTypeFacets nullTypeDefinition);

    T visitArray(ArrayTypeFacets arrayTypeDefinition);

    T visitUnion(UnionTypeFacets unionTypeDefinition);

    T visitTimeOnly(TimeOnlyTypeFacets timeOnlyTypeDefinition);

    T visitJson(JsonSchemaTypeFacets jsonTypeDefinition);

    T visitXml(XmlSchemaTypeFacets xmlTypeDefinition);
}
