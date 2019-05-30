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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.hamcrest.text.IsEqualIgnoringWhiteSpace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.api.RamlModelBuilder;
import org.raml.v2.api.RamlModelResult;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.v2.internal.utils.Dumper;
import org.raml.v2.internal.utils.RamlNodeUtils;
import org.raml.yagi.framework.nodes.Node;

@RunWith(Parameterized.class)
public class RamlBuilderTestCase extends TestDataProvider
{

    public RamlBuilderTestCase(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Test
    public void runTest() throws IOException
    {
        final RamlBuilder builder = new RamlBuilder();
        final Node raml = builder.build(input);
        assertThat(raml, notNullValue());

        dump = Dumper.inMemoryDumper(raml);

        expected = IOUtils.toString(new FileInputStream(this.expectedOutput));
        Assert.assertThat(dump, IsEqualIgnoringWhiteSpace.equalToIgnoringWhiteSpace(expected));
    }

    @Test
    public void crossValidatioTest()
    {
        Node raml = new RamlBuilder().build(input);

        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);


        boolean xor = ramlModelResult.hasErrors() ^ !RamlNodeUtils.getErrors(raml).isEmpty();

        assertFalse(xor);
        assertEquals(ramlModelResult.getValidationResults().size(), RamlNodeUtils.getErrors(raml).size());


    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(RamlBuilderTestCase.class.getResource("").toURI(), "input.raml", "output.txt");
    }

}
