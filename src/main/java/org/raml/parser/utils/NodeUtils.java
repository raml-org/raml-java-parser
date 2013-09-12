package org.raml.parser.utils;

import java.util.List;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;


public class NodeUtils
{

    public static Object getNodeValue(Node node)
    {
        Object value = null;
        if (node instanceof ScalarNode)
        {
            value = ((ScalarNode) node).getValue();
        }
        else if (node instanceof MappingNode)
        {
            List<NodeTuple> nodeTuples = ((MappingNode) node).getValue();
            if (!nodeTuples.isEmpty())
            {
                value = getNodeValue(nodeTuples.get(0).getKeyNode());
            }
        }
        else if (node instanceof SequenceNode)
        {
            List<Node> nodeList = ((SequenceNode) node).getValue();
            if (!nodeList.isEmpty())
            {
                value = getNodeValue(nodeList.get(0));
            }
        }
        return value;
    }
}
