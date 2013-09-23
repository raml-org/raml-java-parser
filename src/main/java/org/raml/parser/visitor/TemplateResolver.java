package org.raml.parser.visitor;

import static org.raml.parser.visitor.IncludeResolver.INCLUDE_TAG;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class TemplateResolver
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private IncludeResolver includeResolver = new IncludeResolver();
    private Map<String, MappingNode> resourceTypesMap = new HashMap<String, MappingNode>();
    private Map<String, MappingNode> traitsMap = new HashMap<String, MappingNode>();
    private ResourceLoader resourceLoader;
    private NodeHandler nodeNandler;

    public TemplateResolver(ResourceLoader resourceLoader, NodeHandler nodeNandler)
    {
        this.resourceLoader = resourceLoader;
        this.nodeNandler = nodeNandler;
    }

    public Map<String, MappingNode> getResourceTypesMap()
    {
        return resourceTypesMap;
    }

    public Map<String, MappingNode> getTraitsMap()
    {
        return traitsMap;
    }

    public void init(MappingNode rootNode)
    {
        for (int i = 0; i < rootNode.getValue().size(); i++)
        {
            NodeTuple rootTuple = rootNode.getValue().get(i);

            String key = ((ScalarNode) rootTuple.getKeyNode()).getValue();
            if (key.equals("resourceTypes") || key.equals("traits"))
            {
                Node templateSequence = rootTuple.getValueNode();
                if (templateSequence.getNodeId() == NodeId.scalar)
                {
                    if (!templateSequence.getTag().startsWith(INCLUDE_TAG))
                    {
                        throw new RuntimeException("Sequence or !include expected: " + templateSequence.getStartMark());
                    }
                    templateSequence = includeResolver.resolveInclude((ScalarNode) templateSequence, resourceLoader, nodeNandler);
                    rootNode.getValue().remove(i);
                    rootNode.getValue().add(i, new NodeTuple(rootTuple.getKeyNode(), templateSequence));
                }

                loopTemplateSequence((SequenceNode) templateSequence, key);
            }
        }
    }

    private void loopTemplateSequence(SequenceNode templateSequence, String templateType)
    {
        List<Node> prunedTmplates = new ArrayList<Node>();
        for (int j = 0; j < templateSequence.getValue().size(); j++)
        {
            Node template = templateSequence.getValue().get(j);
            if (template.getNodeId() == NodeId.scalar)
            {
                if (!template.getTag().startsWith(INCLUDE_TAG))
                {
                    throw new RuntimeException("Mapping or !include expected: " + templateSequence.getStartMark());
                }
                template = includeResolver.resolveInclude((ScalarNode) template, resourceLoader, nodeNandler);
            }
            for (NodeTuple tuple : ((MappingNode) template).getValue())
            {
                String templateKey = ((ScalarNode) tuple.getKeyNode()).getValue();
                MappingNode templateNode = (MappingNode) tuple.getValueNode();
                if (templateType.equals("resourceTypes"))
                {
                    resourceTypesMap.put(templateKey, templateNode);
                    logger.info("adding resource type: " + templateKey);
                }
                if (templateType.equals("traits"))
                {
                    traitsMap.put(templateKey, templateNode);
                    logger.info("adding trait: " + templateKey);
                }
                prunedTmplates.add(getFakeTemplateNode(tuple.getKeyNode()));
            }
        }
        templateSequence.getValue().clear();
        templateSequence.getValue().addAll(prunedTmplates);
    }

    private Node getFakeTemplateNode(Node keyNode)
    {
        List<NodeTuple> innerTuples = new ArrayList<NodeTuple>();
        innerTuples.add(new NodeTuple(new ScalarNode(Tag.STR, "description", null, null, null), keyNode));
        MappingNode innerNode = new MappingNode(Tag.MAP, innerTuples, false);
        List<NodeTuple> outerTuples = new ArrayList<NodeTuple>();
        outerTuples.add(new NodeTuple(keyNode, innerNode));
        return new MappingNode(Tag.MAP, outerTuples, false);
    }
}
