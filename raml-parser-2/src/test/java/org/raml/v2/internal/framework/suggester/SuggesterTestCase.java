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
package org.raml.v2.internal.framework.suggester;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
import org.raml.v2.internal.impl.RamlSuggester;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.yagi.framework.suggester.Suggestion;

import static org.apache.commons.lang.SystemUtils.IS_OS_WINDOWS;

@RunWith(Parameterized.class)
public class SuggesterTestCase extends TestDataProvider
{

    public static final String CURSOR_KEYWORD = "<cursor>";
    public static final String INPUT_FILE_NAME = "input.raml";
    public static final String OUTPUT_FILE_NAME = "output.json";

    public SuggesterTestCase(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Test
    public void verifySuggestion() throws IOException
    {
        final RamlSuggester ramlSuggester = new RamlSuggester();
        String content = IOUtils.toString(new FileInputStream(input), "UTF-8");

        if (IS_OS_WINDOWS)
        {
            content = content.replace(IOUtils.LINE_SEPARATOR_WINDOWS, IOUtils.LINE_SEPARATOR_UNIX);
        }

        final int offset = content.indexOf(CURSOR_KEYWORD);
        final String document = content.substring(0, offset) + content.substring(offset + CURSOR_KEYWORD.length());
        final List<Suggestion> suggestions = ramlSuggester.suggestions(document, offset - 1).getSuggestions();
        final ObjectWriter ow = new ObjectMapper().disableDefaultTyping().writer().withDefaultPrettyPrinter();
        dump = ow.writeValueAsString(suggestions);
        expected = IOUtils.toString(new FileInputStream(this.expectedOutput));
        Assert.assertTrue(jsonEquals(dump, expected));
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(SuggesterTestCase.class.getResource("").toURI(), INPUT_FILE_NAME, OUTPUT_FILE_NAME);
    }
}
