/*
 * Copyright 2013 (c) MuleSoft, Inc.
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
package org.raml.v2.parser;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.emitter.tck.TckEmitter;
import org.raml.yagi.framework.nodes.Node;

public class AbsoluteIncludeTestCase
{

    @Test
    public void absoluteIncludePath()
    {
        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(getRaml(), "some/location/input.raml");
        assertThat(raml, notNullValue());
        String dump = new TckEmitter().dump(raml);
        Assert.assertThat(dump, IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(getExpectedOutput()));
    }

    private String getRaml()
    {
        String includePath = new File("src/test/resources/org/raml/v2/parser/include/nested/description.txt").getAbsolutePath();
        return "#%RAML 1.0\n" +
               "title: absolute include\n" +
               "description: !include " + includePath;
    }

    private String getExpectedOutput()
    {
        return "{\n" +
               "    \"title\": { \"value\": \"absolute include\" },\n" +
               "    \"description\": { \"value\": \"some description\" }\n" +
               "}\n";
    }
}
