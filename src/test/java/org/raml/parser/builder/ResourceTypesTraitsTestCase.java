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
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.QueryParameter;

public class ResourceTypesTraitsTestCase extends AbstractRamlTestCase
{

    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml("org/raml/types/resource-types-traits.yaml");
    }

    @Test
    public void mixed()
    {
        Resource mixed = raml.getResources().get("/mixed");
        assertThat(mixed.getActions().size(), is(1));
        String[] h = {"hAction", "hType1", "hType0", "hTraitA", "hTraitR", "hTraitT1A",
                      "hTraitT1R", "hTraitT0A", "hTraitT0R"};
        Set headers = new HashSet<String>(Arrays.<String>asList(h));
        assertThat(mixed.getAction(ActionType.GET).getHeaders().keySet(), is(headers));
    }

    @Test
    public void override()
    {
        Resource override = raml.getResources().get("/override");
        assertThat(override.getActions().size(), is(1));
        Map<String,QueryParameter> queryParameters = override.getAction(ActionType.GET).getQueryParameters();
        assertThat(queryParameters.size(), is(9));
        assertThat(queryParameters.get("action").getDisplayName(), is("action"));
        assertThat(queryParameters.get("traitOverA").getDisplayName(), is("traitOverA"));
        assertThat(queryParameters.get("traitOverR").getDisplayName(), is("traitOverR"));
        assertThat(queryParameters.get("typeOverR").getDisplayName(), is("typeOverR"));
        assertThat(queryParameters.get("traitOverTypeA").getDisplayName(), is("traitOverTypeA"));
        assertThat(queryParameters.get("traitOverTypeR").getDisplayName(), is("traitOverTypeR"));
        assertThat(queryParameters.get("typeOverType").getDisplayName(), is("typeOverType"));
        assertThat(queryParameters.get("traitOverParentTypeA").getDisplayName(), is("traitOverParentTypeA"));
        assertThat(queryParameters.get("traitOverParentTypeR").getDisplayName(), is("traitOverParentTypeR"));
    }
}
