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
package org.raml;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.GET;
import static org.raml.model.ActionType.HEAD;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.emitter.RamlEmitter;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
import org.raml.model.SecurityScheme;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class EmitterTestCase extends AbstractRamlTestCase
{

    @Test
    public void emitFullConfigFromRaml()
    {
        Raml raml = parseRaml("org/raml/full-config.yaml");
        RamlEmitter emitter = new RamlEmitter();
        String dumpFromRaml = emitter.dump(raml);
        verifyFullDump(raml, dumpFromRaml);
    }

    @Test
    public void emitFullConfigFromAst()
    {
        RamlDocumentBuilder builder = new RamlDocumentBuilder();
        Raml raml = parseRaml("org/raml/full-config.yaml", builder);
        String dumpFromAst = YamlDocumentBuilder.dumpFromAst(builder.getRootNode());
        verifyDump(raml, dumpFromAst);
    }

    @Test
    public void emitConfigWithIncludesFromAst()
    {
        RamlDocumentBuilder builder = new RamlDocumentBuilder();
        Raml raml = parseRaml("org/raml/root-elements-includes.yaml", builder);
        String dumpFromAst = YamlDocumentBuilder.dumpFromAst(builder.getRootNode());
        verifyDump(raml, dumpFromAst);
    }

    @Test
    public void emitEmptyBody()
    {
        Raml raml = parseRaml("org/raml/empty-body.raml");
        RamlEmitter emitter = new RamlEmitter();
        emitter.dump(raml);
    }

    @Test
    public void emitInlineRegexp()
    {
        Raml raml = parseRaml("org/raml/emitter/pattern.yaml");
        RamlEmitter emitter = new RamlEmitter();
        String dump = emitter.dump(raml);
        Raml emittedRaml = parseRaml(dump, "");
        String pattern = emittedRaml.getResource("/hello").getAction("get").getHeaders().get("one").getPattern();
        assertThat(pattern, not(endsWith("\n")));
    }

    @Test
    public void emitRegexpWithPipe()
    {
        Raml raml = parseRaml("org/raml/emitter/pattern-with-newline.yaml");
        RamlEmitter emitter = new RamlEmitter();
        String dump = emitter.dump(raml);
        Raml emittedRaml = parseRaml(dump, "");
        String pattern = emittedRaml.getResource("/hello").getAction("get").getHeaders().get("one").getPattern();
        assertThat(pattern, endsWith("\n"));
    }

    @Test
    public void emitNumbers()
    {
        String yaml = "#%RAML 0.8\n" +
                      "title: numbers\n" +
                      "/resource:\n" +
                      " get:\n" +
                      "  queryParameters:\n" +
                      "   integer:\n" +
                      "    type: integer\n" +
                      "    maximum: 8.0\n" +
                      "    minimum: 1\n" +
                      "   number:\n" +
                      "    type: number\n" +
                      "    maximum: 9.5\n" +
                      "    minimum: 2.0";
        Raml raml = parseRaml(yaml, "");
        RamlEmitter emitter = new RamlEmitter();
        String dump = emitter.dump(raml);
        assertThat(dump, containsString("maximum: 8"));
        assertThat(dump, not(containsString("maximum: 8.0")));
        assertThat(dump, containsString("minimum: 1"));
        assertThat(dump, not(containsString("minimum: 1.0")));
        assertThat(dump, containsString("maximum: 9.5"));
        assertThat(dump, containsString("minimum: 2"));
        assertThat(dump, not(containsString("minimum: 2.0")));
    }

    private Raml verifyDump(Raml source, String dump)
    {
        RamlDocumentBuilder verifier = new RamlDocumentBuilder();
        Raml target = verifier.build(dump, "");

        assertThat(target.getTitle(), is(source.getTitle()));
        assertThat(target.getVersion(), is(source.getVersion()));
        assertThat(target.getBaseUri(), is(source.getBaseUri()));
        assertThat(target.getBaseUriParameters().size(), is(source.getBaseUriParameters().size()));
        assertThat(target.getDocumentation().size(), is(source.getDocumentation().size()));
        assertThat(target.getResources().size(), is(source.getResources().size()));

        return target;
    }

    private void verifyFullDump(Raml source, String dump)
    {
        Raml target = verifyDump(source, dump);

        //*********** URI PARAMETERS ***********

        UriParameter srcHost = source.getBaseUriParameters().get("host");
        UriParameter tgtHost = target.getBaseUriParameters().get("host");
        assertThat(tgtHost.getDisplayName(), is(srcHost.getDisplayName()));
        assertThat(tgtHost.getDescription(), is(srcHost.getDescription()));
        assertThat(tgtHost.getType(), is(srcHost.getType()));
        assertThat(tgtHost.getMinLength(), is(srcHost.getMinLength()));
        assertThat(tgtHost.getMaxLength(), is(srcHost.getMaxLength()));
        assertThat(tgtHost.getPattern(), is(srcHost.getPattern()));

        UriParameter srcPort = source.getBaseUriParameters().get("port");
        UriParameter tgtPort = target.getBaseUriParameters().get("port");
        assertThat(tgtPort.getType(), is(srcPort.getType()));
        assertThat(tgtPort.getMinimum(), is(srcPort.getMinimum()));
        assertThat(tgtPort.getMaximum(), is(srcPort.getMaximum()));

        UriParameter srcPath = source.getBaseUriParameters().get("path");
        UriParameter tgtPath = target.getBaseUriParameters().get("path");
        assertThat(tgtPath.getType(), is(srcPath.getType()));
        assertThat(tgtPath.getEnumeration().size(), is(srcPath.getEnumeration().size()));
        assertThat(tgtPath.getEnumeration().get(0), is(srcPath.getEnumeration().get(0)));
        assertThat(tgtPath.getEnumeration().get(1), is(srcPath.getEnumeration().get(1)));
        assertThat(tgtPath.getEnumeration().get(2), is(srcPath.getEnumeration().get(2)));

        //*********** DOCUMENTATION ***********

        List<DocumentationItem> srcDoc = source.getDocumentation();
        List<DocumentationItem> tgtDoc = target.getDocumentation();
        assertThat(tgtDoc.get(0).getTitle(), is(srcDoc.get(0).getTitle()));
        assertThat(tgtDoc.get(0).getContent(), is(srcDoc.get(0).getContent()));

        //*********** GLOBAL SCHEMAS ***********

        List<Map<String, String>> srcSchemas = source.getSchemas();
        List<Map<String, String>> tgtSchemas = target.getSchemas();
        assertThat(tgtSchemas.size(), is(srcSchemas.size()));
        assertThat(tgtSchemas.get(0).get("league-json"), is(srcSchemas.get(0).get("league-json")));
        assertThat(tgtSchemas.get(1).get("league-xml"), is(srcSchemas.get(1).get("league-xml")));

        //*********** FORM PARAMETERS ***********

        Map<String, List<FormParameter>> srcFormParams = source.getResource("/media").getAction(GET).getBody().get("multipart/form-data").getFormParameters();
        Map<String, List<FormParameter>> tgtFormParams = target.getResource("/media").getAction(GET).getBody().get("multipart/form-data").getFormParameters();
        assertThat(srcFormParams.size(), is(tgtFormParams.size()));
        assertThat(srcFormParams.get("form-1").size(), is(tgtFormParams.get("form-1").size()));
        assertThat(srcFormParams.get("form-1").get(0).getDisplayName(), is(tgtFormParams.get("form-1").get(0).getDisplayName()));
        assertThat(srcFormParams.get("form-2").size(), is(tgtFormParams.get("form-2").size()));
        assertThat(srcFormParams.get("form-2").get(0).getDisplayName(), is(tgtFormParams.get("form-2").get(0).getDisplayName()));

        //*********** RESOURCE TYPES ************

        assertThat(target.getResourceTypes().size(), is(source.getResourceTypes().size()));
        assertThat(target.getResourceTypes().get(0).get("basic").getDisplayName(),
                   is(source.getResourceTypes().get(0).get("basic").getDisplayName()));
        assertThat(target.getResourceTypes().get(1).get("complex").getDisplayName(),
                   is(source.getResourceTypes().get(1).get("complex").getDisplayName()));

        assertThat(target.getResource("/").getType(), is(source.getResource("/").getType()));
        assertThat(target.getResource("/media").getType(), is(source.getResource("/media").getType()));

        //*********** TRAITS ************

        assertThat(target.getTraits().size(), is(source.getTraits().size()));
        assertThat(target.getTraits().get(0).get("simple").getDisplayName(),
                   is(source.getTraits().get(0).get("simple").getDisplayName()));
        assertThat(target.getTraits().get(1).get("knotty").getDisplayName(),
                   is(source.getTraits().get(1).get("knotty").getDisplayName()));

        assertThat(target.getResource("/").getAction(HEAD).getIs(), is(source.getResource("/").getAction(HEAD).getIs()));

        //*********** SECURITY SCHEMES ************

        assertThat(target.getSecuritySchemes().size(), is(source.getSecuritySchemes().size()));
        SecurityScheme tOauth2 = target.getSecuritySchemes().get(0).get("oauth_2_0");
        SecurityScheme sOauth2 = source.getSecuritySchemes().get(0).get("oauth_2_0");
        assertThat(tOauth2.getDescription(), is(sOauth2.getDescription()));
        assertThat(tOauth2.getDescribedBy().getHeaders().size(), is(sOauth2.getDescribedBy().getHeaders().size()));
        assertThat(tOauth2.getDescribedBy().getHeaders().get("Authorization").getDescription(),
                   is(sOauth2.getDescribedBy().getHeaders().get("Authorization").getDescription()));
        assertThat(tOauth2.getDescribedBy().getQueryParameters().size(), is(sOauth2.getDescribedBy().getQueryParameters().size()));
        assertThat(tOauth2.getDescribedBy().getQueryParameters().get("access_token").getDescription(),
                   is(sOauth2.getDescribedBy().getQueryParameters().get("access_token").getDescription()));
        assertThat(tOauth2.getDescribedBy().getResponses().size(), is(sOauth2.getDescribedBy().getResponses().size()));
        assertThat(tOauth2.getDescribedBy().getResponses().get("401").getDescription(),
                   is(sOauth2.getDescribedBy().getResponses().get("401").getDescription()));
        assertThat(tOauth2.getSettings().getAccessTokenUri(), is(sOauth2.getSettings().getAccessTokenUri()));
        assertThat(tOauth2.getSettings().getAccessTokenUri(), is("https://api.dropbox.com/1/oauth2/token"));
        assertThat(tOauth2.getSettings().getAuthorizationUri(), is(sOauth2.getSettings().getAuthorizationUri()));
        assertThat(sOauth2.getSettings().getAuthorizationUri(), is("https://www.dropbox.com/1/oauth2/authorize"));
        assertThat(tOauth2.getSettings().getScopes().size(), is(sOauth2.getSettings().getScopes().size()));
        assertThat(tOauth2.getSettings().getScopes().size(), is(1));
        assertThat(tOauth2.getSettings().getAuthorizationGrants().size(), is(sOauth2.getSettings().getAuthorizationGrants().size()));
        assertThat(tOauth2.getSettings().getAuthorizationGrants().size(), is(2));



        assertThat(target.getResource("/").getSecuredBy().size(), is(source.getResource("/").getSecuredBy().size()));
        assertThat(target.getResource("/").getSecuredBy().get(0).getName(), is(source.getResource("/").getSecuredBy().get(0).getName()));
        assertThat(target.getResource("/").getSecuredBy().get(0).getParameters().size(),
                   is(source.getResource("/").getSecuredBy().get(0).getParameters().size()));
        assertThat(target.getResource("/").getSecuredBy().get(0).getParameters().get("scopes").get(0),
                   is(source.getResource("/").getSecuredBy().get(0).getParameters().get("scopes").get(0)));
        assertThat(target.getResource("/").getSecuredBy().get(1).getName(), is(source.getResource("/").getSecuredBy().get(1).getName()));
        assertThat(target.getResource("/").getSecuredBy().get(2).getName(), is(source.getResource("/").getSecuredBy().get(2).getName()));

        //*********** PROTOCOLS ************

        assertThat(target.getProtocols().size(), is(source.getProtocols().size()));
        assertThat(target.getProtocols().get(0), is(source.getProtocols().get(0)));
        assertThat(target.getProtocols().get(1), is(source.getProtocols().get(1)));
    }

}
