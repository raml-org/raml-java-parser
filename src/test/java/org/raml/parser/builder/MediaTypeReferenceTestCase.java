package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.PUT;

import org.junit.Ignore;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class MediaTypeReferenceTestCase extends AbstractBuilderTestCase
{

    private static final String ramlSource = "org/raml/media-type-reference.yaml";

    @Test
    @Ignore //TODO unify template and mediaType resolvers into ramlPreprocessor
    public void emitter()
    {
        RamlDocumentBuilder builder1 = new RamlDocumentBuilder();
        Raml raml1 = parseRaml(ramlSource, builder1);
        String emitted1 = YamlDocumentBuilder.dumpFromAst(builder1.getRootNode());

        RamlDocumentBuilder builder2 = new RamlDocumentBuilder();
        Raml raml2 = builder2.build(emitted1);

        assertThat(raml2.getResources().get("/simple").getAction(PUT).getBody().size(),
                   is(raml1.getResources().get("/simple").getAction(PUT).getBody().size()));
    }
}
