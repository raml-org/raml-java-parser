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

import org.json.JSONArray;
import org.json.JSONObject;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;

import java.util.List;


public class TypeToJsonSchemaVisitor implements TypeVisitor<JSONObject>
{

    private static final String DEFINITIONS = "definitions";
    private static final String REF = "$ref";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String FORMAT = "format";
    private static final String PROPERTIES = "properties";
    private static final String ANY_OF = "anyOf";

    private static final String OBJECT = "object";
    private static final String NUMBER = "number";
    private static final String INTEGER = "integer";
    private static final String NULL = "null";
    private static final String DATE_TIME = "date-time";
    private static final String STRING = "string";
    private static final String BOOLEAN = "boolean";
    private static final String ARRAY = "array";

    private static final String SCHEMA = "$schema";
    private static final String SCHEMA_VALUE = "http://json-schema.org/draft-04/schema#";

    private JSONObject definitions;


    public TypeToJsonSchemaVisitor()
    {
        this.definitions = new JSONObject();
    }

    public JSONObject transform(final ResolvedType resolvedType)
    {
        final JSONObject root = resolvedType.visit(this);
        root.put(DEFINITIONS, definitions);
        root.put(SCHEMA, SCHEMA_VALUE);
        return root;
    }

    @Override
    public JSONObject visitString(StringResolvedType stringTypeDefinition)
    {
        return new JSONObject().put(TYPE, STRING);
    }

    @Override
    public JSONObject visitObject(ObjectResolvedType objectTypeDefinition)
    {
        final JSONObject typeDefinition = new JSONObject();
        String typeName = getTypeName(objectTypeDefinition);

        // By default add all named types to definitions to allow fully recursive types.
        if (typeName != null && !this.definitions.has(typeName))
        {
            this.definitions.put(typeName, typeDefinition);
            addPropertiesToJsonObject(objectTypeDefinition, typeDefinition);
        }

        // If the type is inline, then the object is created inline.
        if (typeName == null)
        {
            return addPropertiesToJsonObject(objectTypeDefinition, new JSONObject());
        }

        return new JSONObject()
                               .put(TYPE, OBJECT)
                               .put(REF, "#/definitions/" + escapeJsonPointer(typeName));
    }

    private JSONObject addPropertiesToJsonObject(final ObjectResolvedType objectTypeDefinition, JSONObject object)
    {
        JSONObject propertiesObject = new JSONObject();
        object.put(TYPE, OBJECT).put(PROPERTIES, propertiesObject);

        for (String propertyName : objectTypeDefinition.getProperties().keySet())
        {
            PropertyFacets propertyFacets = objectTypeDefinition.getProperties().get(propertyName);

            if (!propertyName.startsWith("/") || !propertyName.endsWith("/"))
            {
                propertiesObject.put(propertyName, propertyFacets.getValueType().visit(this));
            }
        }

        return object;
    }

    private String escapeJsonPointer(final String typeName)
    {
        return typeName.replaceAll("/", "~1");
    }

    private String getTypeName(ObjectResolvedType objectTypeDefinition)
    {
        String typeName = objectTypeDefinition.getTypeName();

        for (TypeId typeId : TypeId.values())
        {
            if (typeId.getType().equals(typeName))
            {
                return null;
            }
        }

        return typeName;
    }

    @Override
    public JSONObject visitBoolean(BooleanResolvedType booleanTypeDefinition)
    {
        return new JSONObject().put(TYPE, BOOLEAN);
    }

    @Override
    public JSONObject visitInteger(IntegerResolvedType integerTypeDefinition)
    {
        return new JSONObject().put(TYPE, INTEGER);
    }

    @Override
    public JSONObject visitNumber(NumberResolvedType numberTypeDefinition)
    {
        return new JSONObject().put(TYPE, NUMBER);
    }

    @Override
    public JSONObject visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition)
    {
        return new JSONObject()
                               .put(TYPE, STRING)
                               .put(FORMAT, DATE_TIME);
    }

    @Override
    public JSONObject visitDate(DateOnlyResolvedType dateOnlyTypeDefinition)
    {
        return new JSONObject()
                               .put(TYPE, STRING)
                               .put(FORMAT, DATE_TIME);
    }

    @Override
    public JSONObject visitDateTime(DateTimeResolvedType dateTimeTypeDefinition)
    {
        return new JSONObject()
                               .put(TYPE, STRING)
                               .put(FORMAT, DATE_TIME);
    }

    @Override
    public JSONObject visitFile(FileResolvedType fileTypeDefinition)
    {
        return new JSONObject().put(TYPE, STRING);
    }

    @Override
    public JSONObject visitNull(NullResolvedType nullTypeDefinition)
    {
        return new JSONObject().put(TYPE, NULL);
    }

    @Override
    public JSONObject visitArray(ArrayResolvedType arrayTypeDefinition)
    {
        return new JSONObject()
                               .put(TYPE, ARRAY)
                               .put(ITEMS, arrayTypeDefinition.getItems().visit(this));
    }

    @Override
    public JSONObject visitUnion(UnionResolvedType unionTypeDefinition)
    {
        final JSONArray unionTypeArray = new JSONArray();
        final List<ResolvedType> of = unionTypeDefinition.of();
        for (ResolvedType resolvedType : of)
        {
            unionTypeArray.put(resolvedType.visit(this));
        }
        return new JSONObject().put(ANY_OF, unionTypeArray);
    }

    @Override
    public JSONObject visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition)
    {
        return new JSONObject().put(TYPE, STRING);
    }

    @Override
    public JSONObject visitJson(JsonSchemaExternalType jsonTypeDefinition)
    {
        throw new IllegalArgumentException("Unsupported type");
    }

    @Override
    public JSONObject visitXml(XmlSchemaExternalType xmlTypeDefinition)
    {
        throw new IllegalArgumentException("Unsupported type");
    }

    @Override
    public JSONObject visitAny(AnyResolvedType anyResolvedType)
    {
        return new JSONObject();
    }

}
