package org.raml.parser.tagresolver;

import static org.raml.parser.tagresolver.JacksonTagResolver.JACKSON_TAG;
import static org.raml.parser.tagresolver.JaxbTagResolver.JAXB_TAG;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * This tag resolver validates that the classes referenced by
 * jackson and jaxb tagged nodes are present.
 */
public class PojoValidatorTagResolver implements TagResolver
{

    @Override
    public boolean handles(Tag tag)
    {
        return JACKSON_TAG.equals(tag) || JAXB_TAG.equals(tag);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        String className = ((ScalarNode) node).getValue();
        try
        {
            Thread.currentThread().getContextClassLoader().loadClass(className);
        }
        catch (ClassNotFoundException e)
        {
            nodeHandler.onCustomTagError(node.getTag(), node, "Class not found " + className);
        }
        return node;
    }

}
