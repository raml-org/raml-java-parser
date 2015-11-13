/*
 * Copyright 2015 (c) SAP SE
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
package org.raml.parser.visitor;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.SecurityScheme;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.builder.AbstractRamlTestCase;

public class RamlDocumentBuilderTestCase extends AbstractRamlTestCase
{

    @Test
    public void parseExtendedModel()
    {
        RamlExt raml = (RamlExt) new RamlDocumentBuilder(RamlExt.class).build("org/raml/parser/visitor/extended.yaml");
        assertThat(raml.getTitle(), is("extended model")); // standard property
        assertThat(raml.getExtension(), is("additional data")); // non-standard property
    }

    @Test
    public void parseModelWithExtensionInExistingKey()
    {
        RamlExt2 raml = (RamlExt2) new RamlDocumentBuilder(RamlExt2.class).build("org/raml/parser/visitor/extended.yaml");
        SecuritySchemeExt scheme = raml.getSecuritySchemesExt().get(0).get("extended");
        assertThat(scheme.getDescription(), is(notNullValue()));
        assertThat(scheme.getExtension().get("key1"), is("foo"));
    }

    public static class RamlExt extends Raml
    {
        private static final long serialVersionUID = 533345138584973337L;

        @Scalar
        private String extension;

        public String getExtension() {
            return extension;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }
    }

    public static class RamlExt2 extends Raml
    {
        private static final long serialVersionUID = 1451208177799874616L;

        @Sequence(alias = "securitySchemes")
        private List<Map<String, SecuritySchemeExt>> securitySchemesExt = new ArrayList<Map<String, SecuritySchemeExt>>();

        public List<Map<String, SecuritySchemeExt>> getSecuritySchemesExt() {
            return securitySchemesExt;
        }

        public void setSecuritySchemesExt(List<Map<String, SecuritySchemeExt>> securitySchemesExt) {
            this.securitySchemesExt = securitySchemesExt;
        }
    }

    public static class SecuritySchemeExt extends SecurityScheme
    {
        private static final long serialVersionUID = -7059558387326732177L;

        @Mapping
        private Map<String,String> extension;

        public Map<String, String> getExtension() {
            return extension;
        }

        public void setExtension(Map<String, String> extension) {
            this.extension = extension;
        }
    }
}
