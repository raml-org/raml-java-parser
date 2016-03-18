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
import org.raml.model.Raml;

public class TemplateParamFunctionsTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/types/template-param-functions.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void resourceTypeDefaultParams()
    {
        assertThat(raml.getResources().get("/users").getDescription(),
                   is("regular users, singular user, plural users"));
    }

    @Test
    public void resourceTypeCustomParams()
    {
        assertThat(raml.getResources().get("/tags").getDescription(),
                   is("irregular plural octopi, " +
                      "irregular singular foot, " +
                      "regular plural dresses, " +
                      "regular singular stress"));
    }

    @Test
    public void includeWithDefaultParam()
    {
        assertThat(raml.getResources().get("/include").getDescription(), is("included description"));
    }

    @Test
    public void camelize()
    {
        assertThat(raml.getResources().get("/camelize").getDescription(), is("single User, double UserName, treble UserNameType"));
    }

    @Test
    public void validate()
    {
        validateRamlNoErrors(ramlSource);
    }
}
