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
import static org.raml.model.ActionType.GET;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;

public class TemplateDefaultParamsTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/types/template-default-params.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void resourceTypeDefaultParams()
    {
        assertThat(raml.getResources().get("/simple").getDescription(),
                   is("resourcePath /simple, resourcePathName simple"));
    }

    @Test
    public void traitDefaultParams()
    {
        assertThat(raml.getResources().get("/simple").getAction(GET).getDescription(),
                   is("resourcePath /simple, resourcePathName simple, methodName get"));
    }

    @Test
    public void resourceTypeDefaultParamsWithUriParam()
    {
        assertThat(raml.getResources().get("/simple").getResources().get("/{simpleId}").getDescription(),
                   is("resourcePath /{simpleId}, resourcePathName simple"));
    }

    @Test
    public void traitDefaultParamsWithUriParam()
    {
        assertThat(raml.getResources().get("/simple").getResources().get("/{simpleId}").getAction(GET).getDescription(),
                   is("resourcePath /{simpleId}, resourcePathName simple, methodName get"));
    }

    @Test
    public void validate()
    {
        validateRamlNoErrors(ramlSource);
    }
}
