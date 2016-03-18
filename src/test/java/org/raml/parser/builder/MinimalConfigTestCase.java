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

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;

public class MinimalConfigTestCase extends AbstractRamlTestCase
{

    @Test
    public void basicConfig()
    {
        Raml raml = parseRaml("org/raml/root-elements.yaml");

        assertThat(raml.getTitle(), is("Sample API"));
        assertThat(raml.getVersion(), is("v1"));
        assertThat(raml.getBaseUri(), is("https://sample.com/api"));

        assertThat(raml.getResources().size(), is(1));
        Resource mediaResource = raml.getResources().get("/media");
        assertThat(mediaResource.getActions().size(), is(1));
        assertThat(mediaResource.getAction(ActionType.GET), is(Action.class));
    }

}
