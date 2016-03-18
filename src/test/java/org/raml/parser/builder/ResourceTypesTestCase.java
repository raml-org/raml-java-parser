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

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;

public class ResourceTypesTestCase extends AbstractRamlTestCase
{

    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml("org/raml/types/resource-types.yaml");
    }

    @Test
    public void simple()
    {

        Resource simpleResource = raml.getResources().get("/simpleResource");
        assertThat(simpleResource.getActions().size(), is(1));
        assertThat(simpleResource.getAction(ActionType.GET).getDescription(), is("some description"));
    }

    @Test
    public void optional()
    {
        Resource optionalResource = raml.getResources().get("/optionalResource");
        assertThat(optionalResource.getActions().size(), is(2));
        assertThat(optionalResource.getAction(ActionType.PUT).getDescription(), is("resource put description"));
        assertThat(optionalResource.getAction(ActionType.PUT).getBody().size(), is(2));
        assertThat(optionalResource.getAction(ActionType.PUT).getBody().containsKey("application/json"), is(true));
        assertThat(optionalResource.getAction(ActionType.PUT).getBody().containsKey("text/xml"), is(true));
        assertThat(optionalResource.getAction(ActionType.POST).getDescription(), is("post description"));
        assertThat(optionalResource.getAction(ActionType.POST).getBody().size(), is(1));
    }

    @Test
    public void parameters()
    {
        Resource paramsResource = raml.getResources().get("/paramsResource");
        assertThat(paramsResource.getActions().size(), is(1));
        assertThat(paramsResource.getAction(ActionType.PATCH).getDescription(), is("homemade description"));

        Resource paramsResource2 = raml.getResources().get("/paramsResource2");
        assertThat(paramsResource2.getActions().size(), is(1));
        assertThat(paramsResource2.getAction(ActionType.DELETE).getDescription(), is("fine description"));
    }

    @Test
    public void inheritance()
    {
        Resource inheritanceResource = raml.getResources().get("/inheritanceResource");
        assertThat(inheritanceResource.getActions().size(), is(2));
        assertThat(inheritanceResource.getAction(ActionType.GET).getDescription(), is("some description"));
        assertThat(inheritanceResource.getAction(ActionType.POST).getBody().size(), is(1));
        assertThat(inheritanceResource.getAction(ActionType.POST).getBody().containsKey("text/yaml"), is(true));
    }

}
