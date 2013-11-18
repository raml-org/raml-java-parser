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
package org.raml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.GET;

import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.raml.emitter.YamlEmitter;
import org.raml.model.DocumentationItem;
import org.raml.model.Raml;
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

        YamlEmitter emitter = new YamlEmitter();
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

    private Raml verifyDump(Raml source, String dump)
    {
        RamlDocumentBuilder verifier = new RamlDocumentBuilder();
        Raml target = verifier.build(dump);

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

        //*********** FORM PARAMETERS ***********

        Map<String, List<FormParameter>> srcFormParams = source.getResource("/media").getAction(GET).getBody().get("multipart/form-data").getFormParameters();
        Map<String, List<FormParameter>> tgtFormParams = target.getResource("/media").getAction(GET).getBody().get("multipart/form-data").getFormParameters();
        assertThat(srcFormParams.size(), is(tgtFormParams.size()));
        assertThat(srcFormParams.get("form-1").size(), is(tgtFormParams.get("form-1").size()));
        assertThat(srcFormParams.get("form-1").get(0).getDisplayName(), is(tgtFormParams.get("form-1").get(0).getDisplayName()));
    }

}
