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
import org.raml.model.Raml;

public class IncludeAbsoluteTestCase extends AbstractRamlTestCase
{

    @Test
    public void absoluteClasspath()
    {
        String location = "org/raml/include/include-main-absolute-classpath.yaml";
        Raml raml = parseRaml(location);
        assertThat(raml.getResource("/main/absolute").getDescription(), is("absolute"));
        assertThat(raml.getResource("/main/absolute/relative").getDescription(), is("relative"));
    }

    @Test
    public void absoluteClasspathValidation()
    {
        String location = "org/raml/include/include-main-absolute-classpath.yaml";
        validateRamlNoErrors(location);
    }

}
