/*
 * Copyright (c) MuleSoft, Inc.
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
import static org.junit.Assert.assertThat;

import java.util.Map;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.Resource;

public class UriParametersTestCase extends AbstractRamlTestCase
{

    public static final String RAML = "org/raml/params/uri-parameters.yaml";

    @Test
    public void validate()
    {
        validateRamlNoErrors(RAML);
    }

    @Test
    public void build()
    {
        Raml raml = parseRaml(RAML);
        Map<String,Resource> namelessChildren = raml.getResources().get("/nameless-children").getResources();
        assertThat(namelessChildren.get("/").getRelativeUri(), is("/"));
        assertThat(namelessChildren.get("/").getUri(), is("/nameless-children/"));
        assertThat(namelessChildren.get("/").getResources().get("/").getRelativeUri(), is("/"));
        assertThat(namelessChildren.get("/").getResources().get("/").getUri(), is("/nameless-children//"));
    }
}
