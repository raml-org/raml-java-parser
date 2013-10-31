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

import org.junit.Ignore;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.yaml.snakeyaml.Yaml;

public class EmitterTestCase extends AbstractRamlTestCase
{

    @Test
    @Ignore //broken due to implicit maps
    public void emitFullConfigFromRaml()
    {
        Raml raml = parseRaml("org/raml/full-config.yaml");

        Yaml yaml = new Yaml();
        String dumpFromRaml = yaml.dump(raml);
        verifyDump(raml, dumpFromRaml);
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

    private void verifyDump(Raml source, String dump)
    {
        RamlDocumentBuilder verifier = new RamlDocumentBuilder();
        Raml target = verifier.build(dump);

        assertThat(source.getTitle(), is(target.getTitle()));
        assertThat(source.getVersion(), is(target.getVersion()));
        assertThat(source.getBaseUri(), is(target.getBaseUri()));
        assertThat(source.getBaseUriParameters().size(), is(target.getBaseUriParameters().size()));
        assertThat(source.getDocumentation().size(), is(target.getDocumentation().size()));
        assertThat(source.getResources().size(), is(target.getResources().size()));
    }

}
