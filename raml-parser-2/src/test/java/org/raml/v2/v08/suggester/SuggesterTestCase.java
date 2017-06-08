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
package org.raml.v2.v08.suggester;

import org.junit.runners.Parameterized;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

public class SuggesterTestCase extends org.raml.v2.internal.framework.suggester.SuggesterTestCase
{
    public SuggesterTestCase(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(SuggesterTestCase.class.getResource("").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME);
    }
}
