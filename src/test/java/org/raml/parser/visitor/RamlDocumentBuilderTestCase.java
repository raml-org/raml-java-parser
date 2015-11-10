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
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.annotation.Scalar;
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
}
