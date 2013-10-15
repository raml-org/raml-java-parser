package org.raml;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.visitor.YamlDocumentBuilder;
import org.yaml.snakeyaml.Yaml;

public class EmitterTestCase
{

    @Test
    @Ignore //broken due to implicit maps
    public void emitFullConfigFromRaml() throws Exception
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/full-config.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

        Yaml yaml = new Yaml();
        String dumpFromRaml = yaml.dump(raml);
        verifyDump(raml, dumpFromRaml);
    }

    @Test
    public void emitFullConfigFromAst() throws Exception
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/full-config.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

        String dumpFromAst = YamlDocumentBuilder.dumpFromAst(ramlSpecBuilder.getRootNode());
        verifyDump(raml, dumpFromAst);
    }

    @Test
    public void emitConfigWithIncludesFromAst() throws Exception
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/root-elements-includes.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

        String dumpFromAst = YamlDocumentBuilder.dumpFromAst(ramlSpecBuilder.getRootNode());
        verifyDump(raml, dumpFromAst);
    }

    private void verifyDump(Raml source, String dump)
    {
        YamlDocumentBuilder<Raml> verifier = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml target = verifier.build(dump);

        assertThat(source.getTitle(), is(target.getTitle()));
        assertThat(source.getVersion(), is(target.getVersion()));
        assertThat(source.getBaseUri(), is(target.getBaseUri()));
        assertThat(source.getBaseUriParameters().size(), is(target.getBaseUriParameters().size()));
        assertThat(source.getDocumentation().size(), is(target.getDocumentation().size()));
        assertThat(source.getResources().size(), is(target.getResources().size()));
    }

}
