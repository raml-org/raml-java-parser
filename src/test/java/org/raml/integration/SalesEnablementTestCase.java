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
package org.raml.integration;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.raml.model.ActionType.GET;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class SalesEnablementTestCase extends AbstractRamlTestCase
{

    private static Raml raml;
    private static final String ramlSource = "org/raml/integration/sales-enablement-api.yaml";

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void schemas()
    {
        Map<String, String> schemas = raml.getConsolidatedSchemas();
        Action post = raml.getResources().get("/presentations").getAction(ActionType.POST);
        assertTrue(schemas.containsKey(post.getBody().get("application/json").getSchema()));
    }

    @Test
    public void presentations()
    {
        Resource simpleResource = raml.getResources().get("/presentations");
        assertThat(simpleResource.getActions().size(), is(2));
        Map<String, QueryParameter> queryParameters = simpleResource.getAction(GET).getQueryParameters();
        assertThat(queryParameters.size(), is(3));
        assertThat(queryParameters.get("title").getDisplayName(), is("title"));
        assertThat(queryParameters.get("start").getDisplayName(), is("start"));
        assertThat(queryParameters.get("pages").getDisplayName(), is("pages"));
    }


    @Test
    public void emitter() throws Exception
    {
        RamlDocumentBuilder builder1 = new RamlDocumentBuilder();
        Raml raml1 = parseRaml(ramlSource, builder1);
        String emitted1 = YamlDocumentBuilder.dumpFromAst(builder1.getRootNode());

        RamlDocumentBuilder builder2 = new RamlDocumentBuilder();
        Raml raml2 = builder2.build(emitted1, "");

        assertThat(raml2.getResources().get("/presentations").getAction(GET).getQueryParameters().size(),
                   is(raml1.getResources().get("/presentations").getAction(GET).getQueryParameters().size()));
    }

    @Test
    public void validation() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(ramlSource));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml, "");
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }
}
