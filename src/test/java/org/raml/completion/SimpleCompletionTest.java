package org.raml.completion;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentSuggestive;

/**
 *
 */
public class SimpleCompletionTest
{

    @Test
    public void simpleRamlSuggestion()
    {
        String header = "#%RAML 0.8\n" +
                        "title: Nat Factory\n";

        YamlDocumentSuggestive<Raml> yamlDocumentSuggestive = new YamlDocumentSuggestive<Raml>(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggestive.suggest(header, "");
        Assert.assertThat(suggest, CoreMatchers.notNullValue());
        Assert.assertThat(suggest.isEmpty(), CoreMatchers.is(false));
    }

    @Test
    public void simpleResource()
    {
        String header = "#%RAML 0.8\n" +
                        "---\n" +
                        "title: Sample API\n" +
                        "version: v1\n" +
                        "baseUri: https://api.sample.com/\n" +
                        "/media:\n" +
                        " displayName: Media\n";

        YamlDocumentSuggestive<Raml> yamlDocumentSuggestive = new YamlDocumentSuggestive<Raml>(new RamlDocumentBuilder());
        List<Suggestion> suggest = yamlDocumentSuggestive.suggest(header, " ");
        Assert.assertThat(suggest, CoreMatchers.notNullValue());
        Assert.assertThat(suggest.isEmpty(), CoreMatchers.is(false));
        Assert.assertTrue(suggest.contains(new DefaultSuggestion("is")));
    }
}
