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
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.emitter.tck.TckEmitter;

@RunWith(Parameterized.class)
public class InternalTckTestCase extends TestDataProvider
{

    private static final String INPUT_FILE_NAME = "input.raml";
    private static final String OUTPUT_FILE_NAME = "output.json";

    public InternalTckTestCase(File input, File expected, String name)
    {
        super(input, expected, name);
    }

    @Test
    public void runTest() throws IOException
    {
        if (!expectedOutput.exists())
        {
            return;
        }

        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());
        List<ErrorNode> errorNodes = raml.findDescendantsWith(ErrorNode.class);
        assertThat(errorNodes, hasSize(0));
        dump = new TckEmitter().dump(raml);
        expected = IOUtils.toString(new FileInputStream(this.expectedOutput));
        Assert.assertTrue(jsonEquals(dump, expected));
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> data() throws URISyntaxException
    {
        Collection<Object[]> data = getData(InternalTckTestCase.class.getResource("").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME);
        data.addAll(getData(InternalTckTestCase.class.getResource("../api").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME));
        return data;
    }

}
