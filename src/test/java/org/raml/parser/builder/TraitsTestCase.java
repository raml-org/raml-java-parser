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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.GET;
import static org.raml.model.ActionType.POST;

import java.util.Map;

import org.junit.Test;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.parameter.QueryParameter;

public class TraitsTestCase extends AbstractRamlTestCase
{

    @Test
    public void addTrait()
    {
        Raml raml = this.parseRaml("org/raml/traits/single-trait-add.yaml");
        Map<String,QueryParameter> queryParams = raml.getResources().get("/media").getAction(GET).getQueryParameters();
        assertThat(queryParams.size(), is(1));
        assertThat(queryParams.get("count").getType(), is(ParamType.INTEGER));
        assertThat(raml.getResources().get("/media").getAction(POST).getQueryParameters().size(), is(2));
    }

    @Test
    public void mergeTrait()
    {
        Raml raml = this.parseRaml("org/raml/traits/single-trait-merge.yaml");
        assertThat(raml.getResources().get("/media").getAction(GET).getQueryParameters().size(), is(2));
    }

    @Test
    public void addOptionalTrait()
    {
        Raml raml = this.parseRaml("org/raml/traits/single-trait-optional-add.yaml");
        assertThat(raml.getResources().get("/media").getAction(GET), nullValue());
    }

    @Test
    public void mergeOptionalTrait()
    {
        Raml raml = this.parseRaml("org/raml/traits/single-trait-optional-merge.yaml");
        assertThat(raml.getResources().get("/media").getAction(GET).getQueryParameters().size(), is(2));
    }

}
