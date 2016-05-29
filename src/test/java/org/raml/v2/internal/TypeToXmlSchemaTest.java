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
package org.raml.v2.internal;

import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.custommonkey.xmlunit.Diff;
import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.internal.impl.v10.type.XmlFacets;
import org.raml.v2.internal.impl.v10.type.ArrayResolvedType;
import org.raml.v2.internal.impl.v10.type.ObjectResolvedType;
import org.raml.v2.internal.impl.v10.type.PropertyFacets;
import org.raml.v2.internal.impl.v10.type.StringResolvedType;
import org.raml.v2.internal.impl.v10.type.TypeToSchemaVisitor;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class TypeToXmlSchemaTest
{

    public static final String SIMPLE_SCENARIO =
            "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\" targetNamespace=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "    <element name=\"user\">\n" +
                    "        <complexType>\n" +
                    "            <choice>\n" +
                    "                <element name=\"name\">\n" +
                    "                    <simpleType>\n" +
                    "                        <restriction base=\"string\"/>\n" +
                    "                    </simpleType>\n" +
                    "                </element>\n" +
                    "                <any minOccurs=\"0\"/>\n" +
                    "            </choice>\n" +
                    "        </complexType>\n" +
                    "    </element>\n" +
                    "</schema>";

    public static final String ATTRIBUTE_EXPECTED =
            "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\" targetNamespace=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "    <element name=\"user\">\n" +
                    "        <complexType>\n" +
                    "            <choice>\n" +
                    "                <any minOccurs=\"0\"/>\n" +
                    "            </choice>\n" +
                    "            <attribute name=\"name\">\n" +
                    "                <simpleType>\n" +
                    "                    <restriction base=\"string\"/>\n" +
                    "                </simpleType>\n" +
                    "            </attribute>\n" +
                    "        </complexType>\n" +
                    "    </element>\n" +
                    "</schema>";

    public static final String NESTED_OBJECT_EXPECTED =
            "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\" targetNamespace=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "    <element name=\"user\">\n" +
                    "        <complexType>\n" +
                    "            <choice>\n" +
                    "                <element name=\"name\">\n" +
                    "                    <simpleType>\n" +
                    "                        <restriction base=\"string\"/>\n" +
                    "                    </simpleType>\n" +
                    "                </element>\n" +
                    "                <element name=\"friend\">\n" +
                    "                    <complexType>\n" +
                    "                        <choice>\n" +
                    "                            <element name=\"name\">\n" +
                    "                                <simpleType>\n" +
                    "                                    <restriction base=\"string\"/>\n" +
                    "                                </simpleType>\n" +
                    "                            </element>\n" +
                    "                        </choice>\n" +
                    "                    </complexType>\n" +
                    "                </element>\n" +
                    "                <any minOccurs=\"0\"/>\n" +
                    "            </choice>\n" +
                    "        </complexType>\n" +
                    "    </element>\n" +
                    "</schema>";
    public static final String ARRAY_EXPECTED =
            "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" attributeFormDefault=\"unqualified\" elementFormDefault=\"unqualified\" targetNamespace=\"http://www.w3.org/2001/XMLSchema\">\n" +
                    "    <element name=\"user\">\n" +
                    "        <complexType>\n" +
                    "            <choice>\n" +
                    "                <element name=\"name\">\n" +
                    "                    <simpleType>\n" +
                    "                        <restriction base=\"string\"/>\n" +
                    "                    </simpleType>\n" +
                    "                </element>\n" +
                    "                <element maxOccurs=\"unbounded\" name=\"friends\">\n" +
                    "                    <complexType>\n" +
                    "                        <choice>\n" +
                    "                            <element name=\"name\">\n" +
                    "                                <simpleType>\n" +
                    "                                    <restriction base=\"string\"/>\n" +
                    "                                </simpleType>\n" +
                    "                            </element>\n" +
                    "                        </choice>\n" +
                    "                    </complexType>\n" +
                    "                </element>\n" +
                    "                <any minOccurs=\"0\"/>\n" +
                    "            </choice>\n" +
                    "        </complexType>\n" +
                    "    </element>\n" +
                    "</schema>";

    @Test
    public void testSimpleScenario() throws IOException, SAXException
    {
        final HashMap<String, PropertyFacets> properties = new LinkedHashMap<>();
        properties.put("name", new PropertyFacets("name", new StringResolvedType(), true));
        final ObjectResolvedType objectTypeFacets = new ObjectResolvedType(new XmlFacets(), null, null, true, null, null, properties);

        validateEquals(objectTypeFacets, SIMPLE_SCENARIO, "user");
    }

    @Test
    public void testAttributeScenario() throws IOException, SAXException
    {
        final HashMap<String, PropertyFacets> properties = new LinkedHashMap<>();
        final StringResolvedType typeFacets = new StringResolvedType(new XmlFacets(true, null, null, null, null), null, null, null, null);
        properties.put("name", new PropertyFacets("name", typeFacets, true));
        final ObjectResolvedType objectTypeFacets = new ObjectResolvedType(new XmlFacets(), null, null, true, null, null, properties);

        validateEquals(objectTypeFacets, ATTRIBUTE_EXPECTED, "user");
    }

    @Test
    public void testNestedObjectScenario() throws IOException, SAXException
    {

        final HashMap<String, PropertyFacets> friendProperties = new LinkedHashMap<>();
        friendProperties.put("name", new PropertyFacets("name", new StringResolvedType(), true));
        final ObjectResolvedType friendType = new ObjectResolvedType(new XmlFacets(), null, null, false, null, null, friendProperties);

        final HashMap<String, PropertyFacets> userProperties = new HashMap<>();
        userProperties.put("name", new PropertyFacets("name", new StringResolvedType(), true));
        userProperties.put("friend", new PropertyFacets("friend", friendType, true));
        final ObjectResolvedType userType = new ObjectResolvedType(new XmlFacets(), null, null, true, null, null, userProperties);

        validateEquals(userType, NESTED_OBJECT_EXPECTED, "user");
    }

    @Test
    public void testArray() throws IOException, SAXException
    {

        final HashMap<String, PropertyFacets> friendProperties = new LinkedHashMap<>();
        friendProperties.put("name", new PropertyFacets("name", new StringResolvedType(), true));
        final ArrayResolvedType friendType = new ArrayResolvedType(new ObjectResolvedType(new XmlFacets(), null, null, false, null, null, friendProperties));

        final HashMap<String, PropertyFacets> userProperties = new LinkedHashMap<>();
        userProperties.put("name", new PropertyFacets("name", new StringResolvedType(), true));
        userProperties.put("friends", new PropertyFacets("friends", friendType, true));
        final ObjectResolvedType userType = new ObjectResolvedType(new XmlFacets(), null, null, true, null, null, userProperties);

        validateEquals(userType, ARRAY_EXPECTED, "user");
    }

    protected void validateEquals(ObjectResolvedType objectTypeFacets, String expected, String name) throws SAXException, IOException
    {
        final TypeToSchemaVisitor typeToSchemaVisitor = new TypeToSchemaVisitor();
        typeToSchemaVisitor.transform(name, objectTypeFacets);
        final XmlSchema user = typeToSchemaVisitor.getSchema();
        final StringWriter writer = new StringWriter();
        user.write(writer);
        final String actual = writer.toString();
        // System.out.println("actual = " + actual);
        final Diff diff = new Diff(actual, expected);
        Assert.assertTrue("Expected : \n" + expected + "\nActual :\n" + actual + "\n" + diff.toString(), diff.identical());
    }
}
