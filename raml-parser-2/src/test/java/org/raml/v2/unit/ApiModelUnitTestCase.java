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
package org.raml.v2.unit;


import org.junit.Test;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;

import java.net.URL;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ApiModelUnitTestCase
{

    @Test
    public void testTypeValidationOfYamlContent() throws Exception
    {
        URL savedRamlLocation = getClass().getClassLoader().getResource("org/raml/v2/unit/validation.raml");
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(savedRamlLocation.toString());
        TypeDeclaration typeDeclaration = ramlModelResult.getApiV10().resources().get(0).methods().get(0).body().get(0);
        List<ValidationResult> validate = typeDeclaration.validate("nam: bla");
        assertThat(validate.get(0).getMessage(), is("Missing required field \"name\""));
    }

    @Test
    public void testTypeValidationWithNullValue()
    {
        URL savedRamlLocation = getClass().getClassLoader().getResource("org/raml/v2/unit/validation.raml");
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(savedRamlLocation.toString());
        TypeDeclaration typeDeclaration = ramlModelResult.getApiV10().resources().get(0).methods().get(0).body().get(0);
        List<ValidationResult> validate = typeDeclaration.validate(null);
        assertThat(validate.get(0).getMessage(), is("Invalid type Null, expected Object"));
    }


    @Test
    public void schemaShouldNotAddAnyWhenAdditionalPropertiesFalse()
    {
        URL savedRamlLocation = getClass().getClassLoader().getResource("org/raml/v2/unit/xml-schema.raml");
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(savedRamlLocation.toString());
        String schema = ramlModelResult.getApiV10().types().get(0).toXmlSchema();
        assertThat(
                schema.trim(),
                is("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" xmlns:tns=\"http://validationnamespace.raml.org\" attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" targetNamespace=\"http://validationnamespace.raml.org\">" +
                   System.lineSeparator()
                   +
                   "    <element name=\"user\" type=\"tns:user\"/>" + System.lineSeparator() +
                   "    <complexType name=\"user\">" + System.lineSeparator() +
                   "        <sequence>" + System.lineSeparator() +
                   "            <element name=\"name\">" + System.lineSeparator() +
                   "                <simpleType>" + System.lineSeparator() +
                   "                    <restriction base=\"string\"/>" + System.lineSeparator() +
                   "                </simpleType>" + System.lineSeparator() +
                   "            </element>" + System.lineSeparator() +
                   "        </sequence>" + System.lineSeparator() +
                   "    </complexType>" + System.lineSeparator() +
                   "</schema>"));


    }


    @Test
    public void shouldGenerateXsdSchemaOnRecursiveTypes()
    {
        URL savedRamlLocation = getClass().getClassLoader().getResource("org/raml/v2/unit/recursive-type.raml");
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(savedRamlLocation.toString());
        String schema = ramlModelResult.getApiV10().types().get(0).toXmlSchema();
        assertThat(
                schema.trim(),
                is("<schema xmlns=\"http://www.w3.org/2001/XMLSchema\" xmlns:tns=\"http://validationnamespace.raml.org\" attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" targetNamespace=\"http://validationnamespace.raml.org\">" +
                   System.lineSeparator()
                   +
                   "    <element name=\"Tree\" type=\"tns:Tree\"/>" + System.lineSeparator() +
                   "    <complexType name=\"Tree\">" + System.lineSeparator() +
                   "        <sequence>" + System.lineSeparator() +
                   "            <element name=\"value\">" + System.lineSeparator() +
                   "                <simpleType>" + System.lineSeparator() +
                   "                    <restriction base=\"string\"/>" + System.lineSeparator() +
                   "                </simpleType>" + System.lineSeparator() +
                   "            </element>" + System.lineSeparator() +
                   "            <element name=\"left\" type=\"tns:Tree\"/>" + System.lineSeparator() +
                   "            <element name=\"right\" type=\"tns:Tree\"/>" + System.lineSeparator() +
                   "            <any maxOccurs=\"unbounded\" minOccurs=\"0\" processContents=\"skip\"/>" + System.lineSeparator() +
                   "        </sequence>" + System.lineSeparator() +
                   "    </complexType>" + System.lineSeparator() +
                   "</schema>"));

    }
}
