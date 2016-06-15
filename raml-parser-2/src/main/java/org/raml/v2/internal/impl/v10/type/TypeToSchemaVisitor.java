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

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAny;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaChoice;
import org.apache.ws.commons.schema.XmlSchemaChoiceMember;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaEnumerationFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMaxLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaMinInclusiveFacet;
import org.apache.ws.commons.schema.XmlSchemaMinLengthFacet;
import org.apache.ws.commons.schema.XmlSchemaPatternFacet;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.constants.Constants;
import org.raml.v2.internal.impl.commons.type.JsonSchemaExternalType;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.XmlSchemaExternalType;

import javax.annotation.Nonnull;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import static org.raml.v2.internal.utils.ValueUtils.asBoolean;
import static org.raml.v2.internal.utils.ValueUtils.defaultTo;
import static org.raml.v2.internal.utils.ValueUtils.isEmpty;

public class TypeToSchemaVisitor implements TypeVisitor<XmlSchemaType>
{
    public static final long UNBOUNDED = Long.MAX_VALUE;
    private XmlSchemaCollection collection;
    private XmlSchema schema;
    private Stack<XmlSchemaElement> currentElement;
    private Map<String, XmlSchemaType> types;

    public TypeToSchemaVisitor()
    {
        this.collection = new XmlSchemaCollection();
        this.schema = new XmlSchema(XMLConstants.W3C_XML_SCHEMA_NS_URI, "raml-xsd", collection);
        this.currentElement = new Stack<>();
        this.types = new HashMap<>();
    }

    public XmlSchema getSchema()
    {
        return schema;
    }

    public XmlSchemaElement transform(String name, ResolvedType resolvedType)
    {
        final boolean empty = currentElement.isEmpty();
        // We use namespace of this element
        if (empty && resolvedType instanceof XmlFacetsCapableType && ((XmlFacetsCapableType) resolvedType).getXmlFacets().getNamespace() != null)
        {
            schema.setTargetNamespace(((XmlFacetsCapableType) resolvedType).getXmlFacets().getNamespace());
        }
        final XmlSchemaElement schemaElement = new XmlSchemaElement(schema, empty);
        schemaElement.setName(name);
        currentElement.push(schemaElement);
        final XmlSchemaType visit = resolvedType.visit(this);
        if (visit != null)
        {
            if (visit.getQName() != null)
            {
                schemaElement.setSchemaTypeName(visit.getQName());
            }
            else
            {
                schemaElement.setSchemaType(visit);
            }
        }
        currentElement.pop();
        return schemaElement;
    }

    @Override
    public XmlSchemaType visitString(StringResolvedType stringTypeDefinition)
    {
        final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(schema, false);
        final XmlSchemaSimpleTypeRestriction content = new XmlSchemaSimpleTypeRestriction();
        content.setBaseTypeName(Constants.XSD_STRING);
        if (stringTypeDefinition.getMinLength() != null)
        {
            final XmlSchemaMinLengthFacet minLength = new XmlSchemaMinLengthFacet();
            minLength.setValue(stringTypeDefinition.getMinLength());
            content.getFacets().add(minLength);
        }

        if (stringTypeDefinition.getMaxLength() != null)
        {
            final XmlSchemaMaxLengthFacet maxLength = new XmlSchemaMaxLengthFacet();
            maxLength.setValue(stringTypeDefinition.getMaxLength());
            content.getFacets().add(maxLength);
        }

        final List<String> enums = stringTypeDefinition.getEnums();
        if (!isEmpty(enums))
        {
            for (String anEnum : enums)
            {
                final XmlSchemaEnumerationFacet enumValue = new XmlSchemaEnumerationFacet();
                enumValue.setValue(anEnum);
                content.getFacets().add(enumValue);
            }
        }

        if (stringTypeDefinition.getPattern() != null)
        {
            final XmlSchemaPatternFacet patternFacet = new XmlSchemaPatternFacet();
            patternFacet.setValue(stringTypeDefinition.getPattern());
            content.getFacets().add(patternFacet);
        }

        simpleType.setContent(content);
        return simpleType;
    }

    @Override
    public XmlSchemaType visitObject(ObjectResolvedType objectTypeDefinition)
    {
        final String typeName = objectTypeDefinition.getTypeName();
        if (typeName != null && types.containsKey(typeName))
        {
            // With this we support recursive structures
            return types.get(typeName);
        }
        else
        {

            final XmlSchemaComplexType value = new XmlSchemaComplexType(schema, typeName != null);
            if (typeName != null)
            {
                value.setName(typeName);
                types.put(typeName, value);
            }
            final XmlSchemaChoice xmlSchemaSequence = new XmlSchemaChoice();
            final List<XmlSchemaChoiceMember> items = xmlSchemaSequence.getItems();
            value.setParticle(xmlSchemaSequence);
            final Map<String, PropertyFacets> properties = objectTypeDefinition.getProperties();
            for (PropertyFacets propertyDefinition : properties.values())
            {

                final ResolvedType valueResolvedType = propertyDefinition.getValueType();
                if (valueResolvedType instanceof XmlFacetsCapableType)
                {

                    final XmlFacets xmlFacets = ((XmlFacetsCapableType) valueResolvedType).getXmlFacets();
                    final String name = defaultTo(xmlFacets.getName(), propertyDefinition.getName());
                    if (asBoolean(xmlFacets.getAttribute(), false))
                    {
                        final XmlSchemaAttribute xmlSchemaAttribute = new XmlSchemaAttribute(schema, false);
                        xmlSchemaAttribute.setName(name);
                        final XmlSchemaType visit = valueResolvedType.visit(this);
                        if (visit instanceof XmlSchemaSimpleType)
                        {
                            if (visit.getQName() != null)
                            {
                                xmlSchemaAttribute.setSchemaTypeName(visit.getQName());
                            }
                            else
                            {
                                xmlSchemaAttribute.setSchemaType((XmlSchemaSimpleType) visit);
                            }
                        }
                        value.getAttributes().add(xmlSchemaAttribute);
                    }
                    else
                    {
                        final XmlSchemaElement schemaElement = transform(name, valueResolvedType);
                        if (!propertyDefinition.isRequired())
                        {
                            // Not required
                            schemaElement.setMinOccurs(0);
                        }
                        items.add(schemaElement);
                    }
                }
            }

            if (asBoolean(objectTypeDefinition.getAdditionalProperties(), true))
            {
                final XmlSchemaAny schemaAny = new XmlSchemaAny();
                schemaAny.setMinOccurs(0);
                schemaAny.setMaxOccurs(UNBOUNDED);
                items.add(schemaAny);
            }
            return value;
        }
    }

    @Override
    public XmlSchemaType visitBoolean(BooleanResolvedType booleanTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_DECIMAL);
    }

    @Override
    public XmlSchemaType visitInteger(IntegerResolvedType integerTypeDefinition)
    {
        return createNumberSchemaType(integerTypeDefinition, Constants.XSD_INTEGER);
    }

    @Override
    public XmlSchemaType visitNumber(NumberResolvedType numberTypeDefinition)
    {
        return createNumberSchemaType(numberTypeDefinition, Constants.XSD_DOUBLE);
    }

    @Nonnull
    protected XmlSchemaType createNumberSchemaType(NumberResolvedType numberTypeDefinition, QName baseType)
    {
        final XmlSchemaSimpleType simpleType = new XmlSchemaSimpleType(schema, false);
        final XmlSchemaSimpleTypeRestriction content = new XmlSchemaSimpleTypeRestriction();
        content.setBaseTypeName(baseType);
        if (numberTypeDefinition.getMinimum() != null)
        {
            final XmlSchemaMinInclusiveFacet minLength = new XmlSchemaMinInclusiveFacet();
            minLength.setValue(numberTypeDefinition.getMinimum());
            content.getFacets().add(minLength);
        }

        if (numberTypeDefinition.getMaximum() != null)
        {
            final XmlSchemaMaxInclusiveFacet maxLength = new XmlSchemaMaxInclusiveFacet();
            maxLength.setValue(numberTypeDefinition.getMaximum());
            content.getFacets().add(maxLength);
        }
        simpleType.setContent(content);
        return simpleType;
    }

    @Override
    public XmlSchemaType visitDateTimeOnly(DateTimeOnlyResolvedType dateTimeOnlyTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_DATETIME);
    }

    @Override
    public XmlSchemaType visitDate(DateOnlyResolvedType dateOnlyTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_DATE);
    }

    @Override
    public XmlSchemaType visitDateTime(DateTimeResolvedType dateTimeTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_DATETIME);
    }

    @Override
    public XmlSchemaType visitFile(FileResolvedType fileTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_BASE64);
    }

    @Override
    public XmlSchemaType visitNull(NullResolvedType nullTypeDefinition)
    {
        this.currentElement.peek().setNillable(true);
        return collection.getTypeByQName(Constants.XSD_ANY);
    }

    @Override
    public XmlSchemaType visitArray(ArrayResolvedType arrayTypeDefinition)
    {
        final ResolvedType itemType = arrayTypeDefinition.getItems();
        final XmlSchemaType visit;
        final XmlFacets xmlFacets = arrayTypeDefinition.getXmlFacets();
        if (asBoolean(xmlFacets.getWrapped(), false))
        {
            // This is for the inside element not the wrapped. So this one is the tag for the item type
            // First uses the xml facet then the item name finally the field name or parent type name
            final String name = defaultTo(defaultTo(((XmlFacetsCapableType) itemType).getXmlFacets().getName(), itemType.getTypeName()), currentElement.peek().getName());
            final XmlSchemaElement transform = transform(name, itemType);
            final XmlSchemaComplexType value = new XmlSchemaComplexType(schema, false);
            final XmlSchemaChoice xmlSchemaSequence = new XmlSchemaChoice();
            value.setParticle(xmlSchemaSequence);
            xmlSchemaSequence.getItems().add(transform);
            visit = value;
        }
        else
        {
            visit = itemType.visit(this);
        }
        final XmlSchemaElement peek = currentElement.peek();
        if (arrayTypeDefinition.getMinItems() != null)
        {
            peek.setMinOccurs(arrayTypeDefinition.getMinItems());
        }

        if (arrayTypeDefinition.getMaxItems() != null)
        {
            peek.setMaxOccurs(arrayTypeDefinition.getMaxItems());
        }
        else
        {
            peek.setMaxOccurs(UNBOUNDED);
        }
        return visit;
    }

    @Override
    public XmlSchemaType visitUnion(UnionResolvedType unionTypeDefinition)
    {
        // TODO we should work this better
        return unionTypeDefinition.of().get(0).visit(this);
    }

    @Override
    public XmlSchemaType visitTimeOnly(TimeOnlyResolvedType timeOnlyTypeDefinition)
    {
        return collection.getTypeByQName(Constants.XSD_TIME);
    }

    @Override
    public XmlSchemaType visitJson(JsonSchemaExternalType jsonTypeDefinition)
    {
        return createAny();
    }

    @Override
    public XmlSchemaType visitXml(XmlSchemaExternalType xmlTypeDefinition)
    {
        return createAny();
    }

    @Override
    public XmlSchemaType visitAny(AnyResolvedType anyResolvedType)
    {

        return createAny();
    }

    @Nonnull
    private XmlSchemaType createAny()
    {
        final XmlSchemaComplexType value = new XmlSchemaComplexType(schema, false);
        final XmlSchemaChoice xmlSchemaSequence = new XmlSchemaChoice();
        value.setParticle(xmlSchemaSequence);
        final List<XmlSchemaChoiceMember> items = xmlSchemaSequence.getItems();
        final XmlSchemaAny schemaAny = new XmlSchemaAny();
        schemaAny.setMinOccurs(0);
        schemaAny.setMaxOccurs(UNBOUNDED);
        items.add(schemaAny);
        return value;
    }
}
