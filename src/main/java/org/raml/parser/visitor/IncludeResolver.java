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

public class IncludeResolver
{

    public static final String INCLUDE_TAG = "tag:raml.org,0.1:include";

    public Node resolveInclude(ScalarNode node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        Node includeNode;
        InputStream inputStream = null;
        try
        {
            String resourceName = node.getValue();
            inputStream = resourceLoader.fetchResource(resourceName);


            if (inputStream == null)
            {
                nodeHandler.onIncludeResourceNotFound(node);
                includeNode = new ScalarNode(Tag.STR, resourceName, node.getStartMark(), node.getEndMark(), node.getStyle());
            }
            else if (resourceName.endsWith(".yaml") || resourceName.endsWith(".yml"))
            {
                Yaml yamlParser = new Yaml();
                includeNode = yamlParser.compose(new InputStreamReader(inputStream));
            }
            else //scalar value
            {
                String newValue = IOUtils.toString(inputStream);
                includeNode = new ScalarNode(Tag.STR, newValue, node.getStartMark(), node.getEndMark(), node.getStyle());
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
}
