package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.PUT;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class TagResolverTestCase extends AbstractRamlTestCase
{

    public static final String RAML = "org/raml/tag-resolver.yaml";

    @Test
    public void customResolver()
    {
        RamlDocumentBuilder builder = new RamlDocumentBuilder(new DefaultResourceLoader(), new CustomTagResolver());
        Raml raml = parseRaml(RAML, builder);
        assertThat(raml.getTitle(), is("custom tag resolved"));
        assertThat(raml.getResources().get("/media").getAction(PUT).getBody().get("application/json").getSchema(), is("custom tag resolved"));
    }

    @Test
    public void include()
    {
        Raml raml = parseRaml(RAML);
        assertThat(raml.getResources().get("/file").getAction(PUT).getBody().get("application/json").getSchema(), containsString("file-json"));
    }

    @Test
    public void validate()
    {
        validateRamlNoErrors(RAML);
    }

    private class CustomTagResolver implements TagResolver
    {
        private final Tag CUSTOM_TAG = new Tag("!custom");

        @Override
        public boolean handles(Tag tag)
        {
            return CUSTOM_TAG.equals(tag);
        }

        @Override
        public Node resolve(Node valueNode, ResourceLoader resourceLoader, NodeHandler nodeHandler)
        {
            return new ScalarNode(Tag.STR, "custom tag resolved", valueNode.getStartMark(), valueNode.getEndMark(), ((ScalarNode) valueNode).getStyle());
        }
    }
}
