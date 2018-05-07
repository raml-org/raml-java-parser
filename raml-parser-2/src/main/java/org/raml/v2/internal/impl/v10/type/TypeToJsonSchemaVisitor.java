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

import java.util.Map;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;

import javax.json.*;
import java.util.HashSet;
import java.util.Set;


public class TypeToJsonSchemaVisitor implements TypeVisitor<JsonObjectBuilder>
{

    private static final String DEFINITIONS = "definitions";
    private static final String REF = "$ref";
    private static final String TYPE = "type";
    private static final String ITEMS = "items";
    private static final String FORMAT = "format";
    private static final String PROPERTIES = "properties";
    private static final String REQUIRED = "required";
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

    private JsonObjectBuilder definitions;
    private JsonBuilderFactory factory;
    private Set<String> definedTypes;


    public TypeToJsonSchemaVisitor()
    {
        this.factory = Json.createBuilderFactory(null);
        this.definitions = this.factory.createObjectBuilder();
        this.definedTypes = new HashSet<>();
    }

    public JsonObject transform(final ResolvedType resolvedType)
    {
        final JsonObjectBuilder root = resolvedType.visit(this);
        root.add(DEFINITIONS, this.definitions);
        root.add(SCHEMA, SCHEMA_VALUE);
        return root.build();
    }

    @Override
    public JsonObjectBuilder visitString(StringResolvedType stringTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, STRING);
    }

    @Override
    public JsonObjectBuilder visitObject(ObjectResolvedType objectTypeDefinition)
    {
        final JsonObjectBuilder typeDefinitionBuilder = this.factory.createObjectBuilder();
        String typeName = getTypeName(objectTypeDefinition);

        // By default add all named types to definitions to allow fully recursive types.
        if (typeName != null && !this.definedTypes.contains(typeName))
        {
            this.definedTypes.add(typeName);
            this.definitions.add(typeName, addPropertiesToJsonObject(objectTypeDefinition, typeDefinitionBuilder));
        }

        // If the type is inline, then the object is created inline.
        if (typeName == null)
        {
            return addPropertiesToJsonObject(objectTypeDefinition, this.factory.createObjectBuilder());
        }

        return this.factory.createObjectBuilder()
                           .add(REF, "#/definitions/" + escapeJsonPointer(typeName));
    }

    private JsonObjectBuilder addPropertiesToJsonObject(final ObjectResolvedType objectTypeDefinition, JsonObjectBuilder objectBuilder)
    {
        final JsonObjectBuilder propertiesBuilder = this.factory.createObjectBuilder();
        final JsonArrayBuilder requiredBuilder = this.factory.createArrayBuilder();

        boolean fieldsRequired = false;

        for (Map.Entry<String, PropertyFacets> entry : objectTypeDefinition.getProperties().entrySet())
        {

            final String propertyName = entry.getKey();
            PropertyFacets propertyFacets = objectTypeDefinition.getProperties().get(propertyName);

            if (!propertyName.startsWith("/") || !propertyName.endsWith("/"))
            {
                propertiesBuilder.add(propertyName, propertyFacets.getValueType().visit(this));
                if (entry.getValue().isRequired())
                {
                    requiredBuilder.add(propertyName);
                    fieldsRequired = true;
                }
            }
        }

        final JsonObjectBuilder builder = objectBuilder.add(TYPE, OBJECT).add(PROPERTIES, propertiesBuilder);
        return fieldsRequired ? builder.add(REQUIRED, requiredBuilder) : builder;
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
    public JsonObjectBuilder visitBoolean(BooleanResolvedType booleanTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, BOOLEAN);
    }

    @Override
    public JsonObjectBuilder visitInteger(IntegerResolvedType integerTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, INTEGER);
    }

    @Override
    public JsonObjectBuilder visitNumber(NumberResolvedType numberTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, NUMBER);
    }

    @Override
    public JsonObjectBuilder visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition)
    {
        return this.factory.createObjectBuilder()
                           .add(TYPE, STRING)
                           .add(FORMAT, DATE_TIME);
    }

    @Override
    public JsonObjectBuilder visitDate(DateOnlyResolvedType dateOnlyTypeDefinition)
    {
        return this.factory.createObjectBuilder()
                           .add(TYPE, STRING)
                           .add(FORMAT, DATE_TIME);
    }

    @Override
    public JsonObjectBuilder visitDateTime(DateTimeResolvedType dateTimeTypeDefinition)
    {
        return this.factory.createObjectBuilder()
                           .add(TYPE, STRING)
                           .add(FORMAT, DATE_TIME);
    }

    @Override
    public JsonObjectBuilder visitFile(FileResolvedType fileTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, STRING);
    }

    @Override
    public JsonObjectBuilder visitNull(NullResolvedType nullTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, NULL);
    }

    @Override
    public JsonObjectBuilder visitArray(ArrayResolvedType arrayTypeDefinition)
    {
        return this.factory.createObjectBuilder()
                           .add(TYPE, ARRAY)
                           .add(ITEMS, arrayTypeDefinition.getItems().visit(this));
    }

    @Override
    public JsonObjectBuilder visitUnion(UnionResolvedType unionTypeDefinition)
    {
        final JsonArrayBuilder unionArrayBuilder = this.factory.createArrayBuilder();

        for (ResolvedType resolvedType : unionTypeDefinition.of())
        {
            unionArrayBuilder.add(resolvedType.visit(this));
        }

        return this.factory.createObjectBuilder().add(ANY_OF, unionArrayBuilder);
    }

    @Override
    public JsonObjectBuilder visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition)
    {
        return this.factory.createObjectBuilder().add(TYPE, STRING);
    }

    @Override
    public JsonObjectBuilder visitJson(JsonSchemaExternalType jsonTypeDefinition)
    {
        throw new IllegalArgumentException("Unsupported type");
    }

    @Override
    public JsonObjectBuilder visitXml(XmlSchemaExternalType xmlTypeDefinition)
    {
        throw new IllegalArgumentException("Unsupported type");
    }

    @Override
    public JsonObjectBuilder visitAny(AnyResolvedType anyResolvedType)
    {
        return this.factory.createObjectBuilder();
    }

}
