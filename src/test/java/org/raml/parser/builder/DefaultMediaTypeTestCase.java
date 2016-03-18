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
import static org.junit.Assert.assertTrue;
import static org.raml.model.ActionType.GET;
import static org.raml.model.ActionType.PUT;

import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class DefaultMediaTypeTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/media-type.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void applyDefault()
    {

        Resource simpleResource = raml.getResources().get("/simple");
        assertThat(simpleResource.getActions().size(), is(4));

        Map<String, MimeType> getBody = simpleResource.getAction(ActionType.GET).getBody();
        assertThat(getBody, nullValue());
        Map<String, MimeType> getResponseBody = simpleResource.getAction(ActionType.GET).getResponses().get("200").getBody();
        assertThat(getResponseBody.size(), is(1));
        assertThat(getResponseBody.containsKey("application/json"), is(true));

        Map<String, MimeType> postBody = simpleResource.getAction(ActionType.POST).getBody();
        assertThat(postBody.size(), is(1));
        assertThat(postBody.containsKey("application/json"), is(true));

        Map<String, MimeType> putBody = simpleResource.getAction(PUT).getBody();
        assertThat(putBody.size(), is(1));
        assertThat(putBody.containsKey("application/json"), is(true));

        Map<String, MimeType> deleteBody = simpleResource.getAction(ActionType.DELETE).getResponses().get("204").getBody();
        assertThat(deleteBody, nullValue());

    }

    @Test
    public void bodyWithFormParams()
    {
        Resource formResource = raml.getResources().get("/form");
        assertThat(formResource.getActions().size(), is(1));

        Map<String, MimeType> postBody = formResource.getAction(ActionType.POST).getBody();
        assertThat(postBody.size(), is(1));
        assertThat(postBody.containsKey("application/json"), is(true));
    }

    @Test
    public void emptyBody()
    {
        Resource resource = raml.getResource("/empty");
        assertThat(resource.getAction(PUT).getBody().size(), is(1));
        assertThat(resource.getAction(PUT).getResponses().get("200").getBody().size(), is(1));
    }

    @Test
    public void noBody()
    {
        Resource resource = raml.getResource("/nobody");
        assertThat(resource.getAction(GET).getBody(), nullValue());
        assertThat(resource.getAction(GET).getResponses().get("200").getBody(), nullValue());
    }

    @Test
    public void validation() throws Exception
    {
        String ramlStream = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(ramlSource));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(ramlStream, "");
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }

    @Test
    public void emitter()
    {
        RamlDocumentBuilder builder1 = new RamlDocumentBuilder();
        Raml raml1 = parseRaml(ramlSource, builder1);
        String emitted1 = YamlDocumentBuilder.dumpFromAst(builder1.getRootNode());

        RamlDocumentBuilder builder2 = new RamlDocumentBuilder();
        Raml raml2 = builder2.build(emitted1, "");

        assertThat(raml2.getResources().get("/simple").getActions().size(),
                   is(raml1.getResources().get("/simple").getActions().size()));
    }
}
