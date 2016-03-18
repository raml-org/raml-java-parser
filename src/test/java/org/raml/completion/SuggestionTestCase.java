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
package org.raml.completion;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.raml.emitter.RamlEmitter.VERSION;

import java.util.List;

import org.junit.Test;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.KeySuggestion;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentSuggester;

public class SuggestionTestCase
{

    public static final int ROOT_SUGGEST_COUNT = 13;
    public static final int RESOURCE_SUGGEST_COUNT = 17;
    public static final int ACTION_SUGGEST_COUNT = 10;
    private static final int BODY_SUGGEST_COUNT = 4;
    private static final int RESPONSES_SUGGEST_COUNT = 8;

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
    public void emptyRaml()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest("", "");
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new DefaultSuggestion(VERSION)), is(true));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void versionHeaderOnly()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(VERSION, "");
        assertThat(suggest.size(), is(ROOT_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("title")), is(true));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(false));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void versionHeaderAndSeparator()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(VERSION + "\n---\n", "");
        assertThat(suggest.size(), is(ROOT_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("title")), is(true));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(false));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void documentSequence()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: hola\n" +
                            "documentation:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "");
        assertThat(suggest.size(), is(ROOT_SUGGEST_COUNT - 2));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void resourceChildrenEmptyBottomSection()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:\n";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, " ", "");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.size(), is(RESOURCE_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("is")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(true));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
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
        assertThat(suggest.size(), is(RESOURCE_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("is")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(true));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void topLevel()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("schemas")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void noDuplicates()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("schemas")), is(true));
        assertThat(suggest.contains(new KeySuggestion("title")), is(false));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void noDuplicatesAfter()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one";

        String bottomSection = "schemas:\n" +
                            " - user: one\n" +
                            "traits:\n" +
                            "baseUri: http://localhost/api";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "", bottomSection);

        assertThat(suggest.size(), is(ROOT_SUGGEST_COUNT - 4));
        assertThat(suggest.contains(new KeySuggestion("title")), is(false));
        assertThat(suggest.contains(new KeySuggestion("schemas")), is(false));
        assertThat(suggest.contains(new KeySuggestion("traits")), is(false));
        assertThat(suggest.contains(new KeySuggestion("baseUri")), is(false));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void noDuplicatesAfterNestedAligned()
    {
        noDuplicatesAfterNestedNoContext("  ");
    }

    @Test
    public void noDuplicatesAfterNestedNotAligned()
    {
        noDuplicatesAfterNestedNoContext(" ");
    }

    @Test
    public void noDuplicatesAfterNestedContextAligned()
    {
        noDuplicatesAfterNestedContext("  p");
    }

    @Test
    public void noDuplicatesAfterNestedContextNotAligned()
    {
        noDuplicatesAfterNestedContext(" p");
    }

    private void noDuplicatesAfterNestedNoContext(String context)
    {
        List<Suggestion> suggest = noDuplicatesAfterNested(context);
        assertThat(suggest.size(), is(RESOURCE_SUGGEST_COUNT - 4));
        assertThat(suggest.contains(new KeySuggestion("get")), is(false));
        assertThat(suggest.contains(new KeySuggestion("post")), is(false));
        assertThat(suggest.contains(new KeySuggestion("put")), is(false));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(false));
        assertThat(suggest.contains(new KeySuggestion("patch")), is(true));
    }

    private void noDuplicatesAfterNestedContext(String context)
    {
        List<Suggestion> suggest = noDuplicatesAfterNested(context);
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new KeySuggestion("patch")), is(true));
    }

    private List<Suggestion> noDuplicatesAfterNested(String context)
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/resource:\n" +
                            "  get:\n" +
                            "  post:\n" +
                            "    body:\n" +
                            "      text/plain:\n" +
                            "        example: hi\n";

        String bottomSection = "  put:\n" +
                               "    body:\n" +
                               "  delete:\n" +
                               "/another:\n" +
                               "  patch:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, context, bottomSection);
        assertThat(suggest.get(0).getIndentation(), is(2));
        return suggest;
    }

    @Test
    public void resource()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, " ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("is")), is(true));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.contains(new KeySuggestion("get")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void resourceWithDeleteContext()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, " del");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(1));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void action()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(ACTION_SUGGEST_COUNT - 1));
        assertThat(suggest.contains(new KeySuggestion("headers")), is(false));
        assertThat(suggest.contains(new KeySuggestion("queryParameters")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(2));
    }

    @Test
    public void actionHeaderParam()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER, "    ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.contains(new KeySuggestion("required")), is(true));
        assertThat(suggest.contains(new KeySuggestion("default")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void actionBody()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:\n" +
                            "  put:\n" +
                            "    body:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "      ");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.size(), is(BODY_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("application/json")), is(true));
        assertThat(suggest.contains(new KeySuggestion("application/xml")), is(true));
        assertThat(suggest.contains(new KeySuggestion("application/x-www-form-urlencoded")), is(true));
        assertThat(suggest.contains(new KeySuggestion("multipart/form-data")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void responseBody()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:\n" +
                            " put:\n" +
                            "  responses:\n" +
                            "   200:\n" +
                            "    body:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "     ");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.size(), is(BODY_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("application/json")), is(true));
        assertThat(suggest.contains(new KeySuggestion("application/xml")), is(true));
        assertThat(suggest.contains(new KeySuggestion("application/x-www-form-urlencoded")), is(true));
        assertThat(suggest.contains(new KeySuggestion("multipart/form-data")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void responseStatusCodes()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: one\n" +
                            "/ResourceName:\n" +
                            " put:\n" +
                            "  responses:";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "   ");
        assertThat(suggest.isEmpty(), is(false));
        assertThat(suggest.size(), is(RESPONSES_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("200")), is(true));
        assertThat(suggest.contains(new KeySuggestion("201")), is(true));
        assertThat(suggest.contains(new KeySuggestion("400")), is(true));
        assertThat(suggest.contains(new KeySuggestion("404")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

    @Test
    public void nonAlignedPosition()
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(HEADER_FOUR_SPACE, "  ");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(RESOURCE_SUGGEST_COUNT - 3));
        assertThat(suggest.contains(new KeySuggestion("delete")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(4));
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
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void scalarInclude()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: !include title.txt";

        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "", "");
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(ROOT_SUGGEST_COUNT - 1));
        assertThat(suggest.contains(new KeySuggestion("version")), is(true));
        assertThat(suggest.contains(new KeySuggestion("title")), is(false));
        assertThat(suggest.get(0).getIndentation(), is(0));
    }

    @Test
    public void afterComment()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: comment\n" +
                            "/name:\n" +
                            "# methods";
        String bottomSection = "# one" +
                               " # more" +
                               "   # comment # plus # comment ###";

        validateRamlWithComments(topSection, bottomSection);
    }

    @Test
    public void afterInlineComment()
    {
        String topSection = "#%RAML 0.8\n" +
                            "title: comment\n" +
                            "/name: # name resource";
        String bottomSection = "";

        validateRamlWithComments(topSection, bottomSection);
    }

    private void validateRamlWithComments(String topSection, String bottomSection)
    {
        YamlDocumentSuggester yamlDocumentSuggester = new YamlDocumentSuggester(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggester.suggest(topSection, "  ", bottomSection);
        assertThat(suggest, notNullValue());
        assertThat(suggest.size(), is(RESOURCE_SUGGEST_COUNT));
        assertThat(suggest.contains(new KeySuggestion("get")), is(true));
        assertThat(suggest.contains(new KeySuggestion("post")), is(true));
        assertThat(suggest.get(0).getIndentation(), is(-1));
    }

}
