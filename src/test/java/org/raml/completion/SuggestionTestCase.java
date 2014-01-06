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
import static org.raml.emitter.RamlEmitter.VERSION;

import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.KeySuggestion;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentSuggester;

public class SuggestionTestCase
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

    @Test
    public void emptyRamlSuggestion()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest("", "");
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new DefaultSuggestion(VERSION)), is(true));
    }

    @Test
    @Ignore
    public void headerOnlyRamlSuggestion()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(VERSION, "");
        assertThat(suggest.size(), is(10));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
    }

    @Test
    public void mappingWithTrailingSpace()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName: ";
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, " ");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.size(), is(16));
        assertThat(suggest.contains(new KeySuggestion("is")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(true));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
    }

    @Test
    public void simpleRamlSuggestion()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("schemas")), is(true));

    }

    @Test
    public void simpleRamlNoDupsSuggestion()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("schemas")), is(true));
        assertThat(suggest.contains(new KeySuggestion("title")), is(false));
    }

    @Test
    public void simpleResource()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, " ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("is")), is(true));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(false));
    }

    @Test
    public void simpleResourceWithDeleteContext()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, " del");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
    }

    @Test
    public void simpleRamlWithAction()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("headers")), is(false));
        assertThat(suggest.contains(new KeySuggestion("queryParameters")), is(true));

    }

    @Test
    public void simpleRamlWithParam()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "    ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new KeySuggestion("required")));
        Assert.assertTrue(suggest.contains(new KeySuggestion("default")));
    }

    @Test
    public void simpleRamlWithNonAlignedPosition()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER_FOUR_SPACE, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new KeySuggestion("delete")));
    }

    @Test
    public void simpleRamlBroken()
    {
        String topSection = "#%RAML 0.8\n" +
                            "tilte: Sample API\n" +
                            "versionn: one";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        Assert.assertTrue(suggest.contains(new KeySuggestion("version")));
    }


}
