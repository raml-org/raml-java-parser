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
package org.raml.tagresolver;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.PATCH;
import static org.raml.model.ActionType.POST;
import static org.raml.model.ActionType.PUT;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class PojoValidatorTestCase extends AbstractRamlTestCase
{

    @Test
    public void pojoNotFound()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/tagresolver/pojo-validator-not-found.yaml");
        assertThat(validationResults.size(), is(2));
        assertThat(validationResults.get(0).getMessage(), containsString("Class not found org.raml.tagresolver.user"));
        assertThat(validationResults.get(1).getMessage(), containsString("Class not found org.raml.tagresolver.Users"));
    }

    @Test
    public void pojoFound()
    {
        validateRamlNoErrors("org/raml/tagresolver/pojo-validator-found.yaml");
    }

    @Test
    public void build()
    {
        Raml raml = parseRaml("org/raml/tagresolver/pojo-validator-found.yaml");

        assertThat(raml.getSchemas().size(), is(2));
        assertThat(raml.getSchemas().get(0).get("userjson"), containsString("\"username\":{\"type\":\"string\",\"required\":true}"));
        assertThat(raml.getSchemas().get(1).get("userxml"), containsString("<xs:attribute name=\"username\" type=\"xs:string\" use=\"required\"/>"));

        Map<ActionType,Action> actions = raml.getResources().get("/resource").getActions();

        assertThat(actions.get(POST).getBody().get("application/json").getSchema(), containsString("\"username\":{\"type\":\"string\",\"required\":true}"));
        assertThat(actions.get(POST).getBody().get("text/xml").getSchema(), containsString("<xs:attribute name=\"username\" type=\"xs:string\" use=\"required\"/>"));

        assertThat(actions.get(PUT).getBody().get("application/json").getSchema(), containsString("\"username\":{\"type\":\"string\",\"required\":true}"));
        assertThat(actions.get(PUT).getBody().get("text/xml").getSchema(), containsString("<xs:attribute name=\"username\" type=\"xs:string\" use=\"required\"/>"));

        assertThat(actions.get(PATCH).getBody().get("application/json").getSchema(), containsString("userjson"));
        assertThat(actions.get(PATCH).getBody().get("text/xml").getSchema(), containsString("userxml"));
    }
}
