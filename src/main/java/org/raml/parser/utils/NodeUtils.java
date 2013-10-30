package org.raml.parser.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;


public class NodeUtils
{

    private static Set STANDARD_TAGS = new HashSet(
            Arrays.asList(new Tag[] {
                    Tag.YAML,
                    Tag.VALUE,
                    Tag.MERGE,
                    Tag.SET,
                    Tag.PAIRS,
                    Tag.OMAP,
                    Tag.BINARY,
                    Tag.INT,
                    Tag.FLOAT,
                    Tag.TIMESTAMP,
                    Tag.BOOL,
                    Tag.NULL,
                    Tag.STR,
                    Tag.SEQ,
                    Tag.MAP
            }));

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

    public static boolean isStandardTag(Tag tag)
    {
        return STANDARD_TAGS.contains(tag);
    }
}
