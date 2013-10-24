package org.raml.parser.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class IncludeResolver implements TagResolver
{

    public static final Tag INCLUDE_TAG = new Tag("!include");

    @Override
    public boolean handles(Tag tag)
    {
        return INCLUDE_TAG.equals(tag);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        Node includeNode;
        InputStream inputStream = null;
        try
        {
            ScalarNode scalarNode = (ScalarNode) node;
            String resourceName = scalarNode.getValue();
            inputStream = resourceLoader.fetchResource(resourceName);


            if (inputStream == null)
            {
                nodeHandler.onCustomTagError(INCLUDE_TAG, node, "Include can not be resolved " + resourceName);
                includeNode = new ScalarNode(Tag.STR, resourceName, node.getStartMark(), node.getEndMark(), scalarNode.getStyle());
            }
            else if (resourceName.endsWith(".raml") || resourceName.endsWith(".yaml") || resourceName.endsWith(".yml"))
            {
                Yaml yamlParser = new Yaml();
                includeNode = yamlParser.compose(new InputStreamReader(inputStream));
            }
            else //scalar value
            {
                String newValue = IOUtils.toString(inputStream);
                includeNode = new IncludeScalarNode(resourceName, newValue, scalarNode);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                //ignore
            }
        }
        return includeNode;
    }

    public static class IncludeScalarNode extends ScalarNode
    {
        private String includeName;

        public IncludeScalarNode(String includeName, String value, ScalarNode node)
        {
            super(Tag.STR, value, node.getStartMark(), node.getEndMark(), node.getStyle());
            this.includeName = includeName;
        }

        public String getIncludeName()
        {
            return includeName;
        }
    }
}
