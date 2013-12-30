/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.completion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentSuggester;

public class SimpleCompletionTest
{

    public static final String HEADER = "#%RAML 0.8\n" +
                                        "---\n" +
                                        "title: Sample API\n" +
                                        "version: v1\n" +
                                        "baseUri: https://api.sample.com/\n" +
                                        "/media:\n" +
                                        " displayName: Media\n" +
                                        " get:\n" +
                                        " put:\n" +
                                        "  headers:\n" +
                                        "   hi:";
    public static final String HEADER_FOUR_SPACE = "#%RAML 0.8\n" +
                                                   "---\n" +
                                                   "title: Sample API\n" +
                                                   "version: v1\n" +
                                                   "baseUri: https://api.sample.com/\n" +
                                                   "/media:\n" +
                                                   "    displayName: Media\n" +
                                                   "    get:\n" +
                                                   "    put:";
    public static final String HEADER_BROKEN = "#%RAML 0.8\n" +
                                               "---\n" +
                                               "tilte: Sample API\n" +
                                               "versionn: one";
    @Test
    public void simpleRamlSuggestion()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new DefaultSuggestion("schemas")), is(true));

    }

    @Test
    public void simpleResource()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, " ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new DefaultSuggestion("is")), is(true));
        assertThat(suggest.contains(new DefaultSuggestion("delete")), is(true));
    }

    @Test
    public void simpleResourceWithDeleteContext()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, " del");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new DefaultSuggestion("delete")), is(true));
    }

    @Test
    public void simpleRamlWithAction()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("headers")));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("queryParameters")));
    }

    @Test
    public void simpleRamlWithParam()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "    ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("required")));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("default")));
    }

    @Test
    public void simpleRamlWithNonAlignedPosition()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER_FOUR_SPACE, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("delete")));
    }

    @Test
    public void simpleRamlBroken()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER_BROKEN, "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("version")));
    }


}
