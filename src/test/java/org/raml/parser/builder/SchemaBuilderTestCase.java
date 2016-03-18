/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.GET;

import javax.xml.validation.Schema;

import org.junit.Test;
import org.raml.model.MimeType;
import org.raml.model.Raml;

public class SchemaBuilderTestCase extends AbstractRamlTestCase
{

    @Test
    public void xsdWithInclude()
    {
        Raml raml = parseRaml("org/raml/schema/xsd-includer.raml");
        MimeType mimeType = raml.getResources().get("/name").getAction(GET).getResponses().get("200").getBody().get("application/xml");
        assertThat(mimeType.getCompiledSchema(), is(Schema.class));
        assertThat(mimeType.getSchema(), is(String.class));
        assertThat(mimeType.getSchema(), containsString("include schemaLocation=\"xsd-include.xsd\""));
    }

    @Test
    public void globalXsdWithInclude()
    {
        Raml raml = parseRaml("org/raml/schema/xsd-global-includer.raml");
        MimeType mimeType = raml.getResources().get("/name").getAction(GET).getResponses().get("200").getBody().get("application/xml");
        assertThat(raml.getCompiledSchemas().size(), is(1));
        assertThat(raml.getCompiledSchemas().get(mimeType.getSchema()), is(Schema.class));
        assertThat(mimeType.getSchema(), is(String.class));
        assertThat(mimeType.getSchema(), is("name-schema"));
    }


    @Test
    public void jsonSchemaWithRef()
    {
        Raml raml = parseRaml("org/raml/schema/json-schema-ref.raml");
        MimeType mimeType = raml.getResources().get("/name").getAction(GET).getResponses().get("200").getBody().get("application/json");
        assertThat(mimeType.getCompiledSchema(), is(String.class));
        assertThat(mimeType.getSchema(), is(String.class));
        assertThat(mimeType.getSchema(), containsString("draft-04"));
    }

    @Test
    public void globalJsonSchemaWithRef()
    {
        Raml raml = parseRaml("org/raml/schema/json-schema-global-ref.raml");
        MimeType mimeType = raml.getResources().get("/name").getAction(GET).getResponses().get("200").getBody().get("application/json");
        assertThat(raml.getCompiledSchemas().size(), is(1));
        Object globalCompiledSchema = raml.getCompiledSchemas().get(mimeType.getSchema());
        assertThat(globalCompiledSchema, is(String.class));
        assertThat((String) globalCompiledSchema, is("org/raml/schema/refs/fstab-referring.json"));
        assertThat(mimeType.getSchema(), is(String.class));
        assertThat(mimeType.getSchema(), is("name-schema"));
    }

}
