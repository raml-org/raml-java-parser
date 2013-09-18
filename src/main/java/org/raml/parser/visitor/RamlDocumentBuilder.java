package org.raml.parser.visitor;

import static org.raml.parser.visitor.IncludeResolver.INCLUDE_TAG;
import static org.yaml.snakeyaml.nodes.NodeId.mapping;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class RamlDocumentBuilder extends YamlDocumentBuilder<Raml>
{

    public static final String OPTIONAL_MODIFIER = "?";
    public static final String ALL_ACTIONS = "*";
    public static final String TRAIT_USE_KEY = "is";
    public static final String RESOURCE_TYPE_USE_KEY = "type";
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, MappingNode> resourceTypesMap = new HashMap<String, MappingNode>();
    private Map<String, MappingNode> traitsMap = new HashMap<String, MappingNode>();
    private IncludeResolver includeResolver = new IncludeResolver();

    private enum TemplateType
    {
        RESOURCE_TYPE, TRAIT
    }

    public RamlDocumentBuilder()
    {
        super(Raml.class);
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        super.onMappingNodeStart(mappingNode);
        if (getDocumentContext().peek() instanceof Resource)
        {
            mergeTemplatesIfNeeded(mappingNode, new HashMap<String, Node>());
            removeOptionalNodes(mappingNode);
        }
    }

    private void mergeTemplatesIfNeeded(MappingNode resourceNode, Map<String, Node> globalActionNodes)
    {
        Node typeReference = null;
        Map<String, SequenceNode> traitsReference = new HashMap<String, SequenceNode>();
        HashMap<String, Node> actionNodes = new HashMap<String, Node>(globalActionNodes);

        for (NodeTuple resourceTuple : resourceNode.getValue())
        {
            String key = ((ScalarNode) resourceTuple.getKeyNode()).getValue();
            if (key.equals(RESOURCE_TYPE_USE_KEY))
            {
                typeReference = resourceTuple.getValueNode();
            }
            else if (key.equals(TRAIT_USE_KEY))
            {
                traitsReference.put(ALL_ACTIONS, (SequenceNode) resourceTuple.getValueNode());
            }
            else if (isAction(key))
            {
                Node actionNode = resourceTuple.getValueNode();
                if (actionNode.getNodeId() != mapping)
                {
                    actionNode = setTupleValueToEmptyMappingNode(resourceTuple);
                }
                actionNodes.put(normalizeKey(key), actionNode);
                for (NodeTuple actionTuple : ((MappingNode) actionNode).getValue())
                {
                    String actionTupleKey = ((ScalarNode) actionTuple.getKeyNode()).getValue();
                    if (actionTupleKey.equals(TRAIT_USE_KEY))
                    {
                        traitsReference.put(normalizeKey(key), (SequenceNode) actionTuple.getValueNode());
                    }
                }
            }

        }

        if (!traitsReference.isEmpty())
        {
            //merge action level tratis
            for (String actionName : traitsReference.keySet())
            {
                if (!actionName.equals(ALL_ACTIONS))
                {
                    applyTraitsToActions(traitsReference.get(actionName), actionNodes, actionName);
                }
            }

            //merge resource level traits if there is no parent resource type
            if (traitsReference.get(ALL_ACTIONS) != null && typeReference == null)
            {
                applyTraitsToActions(traitsReference.get(ALL_ACTIONS), actionNodes, null);
            }
        }

        if (typeReference != null)
        {
            MappingNode clone = cloneTemplate(typeReference, TemplateType.RESOURCE_TYPE);

            //update global action map
            for (String key : actionNodes.keySet())
            {
                if (!globalActionNodes.containsKey(key))
                {
                    globalActionNodes.put(key, actionNodes.get(key));
                }
            }

            //merge parent type if defined
            mergeTemplatesIfNeeded(clone, actionNodes);

            //merge resource level traits
            if (traitsReference.get(ALL_ACTIONS) != null)
            {
                applyTraitsToActions(traitsReference.get(ALL_ACTIONS), actionNodes, null);
            }

            //merge type, no traits (action level traits could be merged)
            mergeNodes(resourceNode, clone, Resource.class);
        }
    }

    private Node setTupleValueToEmptyMappingNode(NodeTuple tuple)
    {
        try
        {
            Field value = tuple.getClass().getDeclaredField("valueNode");
            value.setAccessible(true);
            MappingNode mappingNode = new MappingNode(Tag.MAP, new ArrayList<NodeTuple>(), false);
            value.set(tuple, mappingNode);
            return mappingNode;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    private void applyTraitsToActions(SequenceNode traits, Map<String, Node> actionNodes, String actionName)
    {
        for (Node ref : traits.getValue())
        {
            MappingNode clone = cloneTemplate(ref, TemplateType.TRAIT);
            if (actionName == null)
            {
                for (Node actionNode : actionNodes.values())
                {
                    mergeNodes(actionNode, clone, Action.class);
                }
            }
            else
            {
                mergeNodes(actionNodes.get(actionName), clone, Action.class);
            }
        }
    }

    private MappingNode cloneTemplate(Node reference, TemplateType type)
    {
        String templateName = getTemplateName(reference);
        Map<String, MappingNode> templateMap;
        String label;
        if (type == TemplateType.RESOURCE_TYPE)
        {
            templateMap = resourceTypesMap;
            label = "resource type";
        }
        else
        {
            templateMap = traitsMap;
            label = "trait";
        }

        MappingNode templateNode = templateMap.get(templateName);
        if (templateNode == null)
        {
            throw new RuntimeException(label + " not defined: " + templateName);
        }
        Map<String, String> parameters = getTemplateParameters(reference);
        return cloneMappingNode(templateNode, parameters);
    }


    private void removeOptionalNodes(MappingNode node)
    {
        for (NodeTuple tuple : new ArrayList<NodeTuple>(node.getValue()))
        {
            if (isOptional(((ScalarNode) tuple.getKeyNode()).getValue()))
            {
                node.getValue().remove(tuple);
            }
            else
            {
                if (tuple.getValueNode().getNodeId() == mapping)
                {
                    removeOptionalNodes((MappingNode) tuple.getValueNode());
                }
            }
        }
    }

    private MappingNode cloneMappingNode(MappingNode node, Map<String, String> parameters)
    {
        List<NodeTuple> tuples = new ArrayList<NodeTuple>();
        for (NodeTuple tuple : node.getValue())
        {
            Node key = cloneScalarNode((ScalarNode) tuple.getKeyNode(), parameters);
            Node value = cloneNode(tuple.getValueNode(), parameters);
            tuples.add(new NodeTuple(key, value));
        }
        return new MappingNode(node.getTag(), tuples, node.getFlowStyle());
    }

    private Node cloneNode(Node valueNode, Map<String, String> parameters)
    {
        if (valueNode.getNodeId() == NodeId.mapping)
        {
            return cloneMappingNode((MappingNode) valueNode, parameters);
        }
        else if (valueNode.getNodeId() == NodeId.sequence)
        {
            return cloneSequenceNode((SequenceNode) valueNode, parameters);
        }
        else if (valueNode.getNodeId() == NodeId.scalar)
        {
            return cloneScalarNode((ScalarNode) valueNode, parameters);
        }
        throw new RuntimeException("unsupported node type: " + valueNode.getNodeId());
    }

    private SequenceNode cloneSequenceNode(SequenceNode node, Map<String, String> parameters)
    {
        List<Node> nodes = new ArrayList<Node>();
        for (Node item : node.getValue())
        {
            nodes.add(cloneNode(item, parameters));
        }
        return new SequenceNode(node.getTag(), nodes, node.getFlowStyle());
    }

    private ScalarNode cloneScalarNode(ScalarNode node, Map<String, String> parameters)
    {
        String value = node.getValue();
        for (String key : parameters.keySet())
        {
            value = value.replaceAll("<<" + key + ">>", parameters.get(key));
        }
        return new ScalarNode(node.getTag(), value, node.getStartMark(), node.getEndMark(), node.getStyle());
    }

    private Map<String, String> getTemplateParameters(Node node)
    {
        //TODO no default params for now...
        Map<String, String> parameters = new HashMap<String, String>();
        if (node.getNodeId() == NodeId.mapping)
        {
            List<NodeTuple> tuples = ((MappingNode) node).getValue();
            Node params = tuples.get(0).getValueNode();
            for (NodeTuple paramTuple : ((MappingNode) params).getValue())
            {
                String paramKey = ((ScalarNode) paramTuple.getKeyNode()).getValue();
                String paramValue = ((ScalarNode) paramTuple.getValueNode()).getValue();
                parameters.put(paramKey, paramValue);
            }
        }
        return parameters;
    }

    private String getTemplateName(Node templateReferenceNode)
    {
        Node templateNameNode = templateReferenceNode;
        if (templateReferenceNode.getNodeId() == mapping)
        {
            templateNameNode = ((MappingNode) templateReferenceNode).getValue().get(0).getKeyNode();
        }
        return ((ScalarNode) templateNameNode).getValue();
    }

    private MappingNode mergeMappingNodes(MappingNode baseNode, MappingNode templateNode, Class<?> context)
    {

        Map<String, NodeTuple> baseTupleMap = getTupleMap(baseNode);
        for (NodeTuple templateTuple : templateNode.getValue())
        {
            String templateKey = ((ScalarNode) templateTuple.getKeyNode()).getValue();

            if (!nonMergeableFields(context).contains(templateKey))
            {
                String baseKey = getMatchingKey(baseTupleMap, templateKey);
                if (baseKey == null)
                {
                    //TODO may require cleaning of value node
                    baseNode.getValue().add(templateTuple);
                }
                else
                {
                    Node keyNode = baseTupleMap.get(baseKey).getKeyNode();
                    if (isOptional(baseKey) && !isOptional(templateKey))
                    {
                        keyNode = templateTuple.getKeyNode();
                    }
                    Node baseInnerNode = baseTupleMap.get(baseKey).getValueNode();
                    Node templateInnerNode = templateTuple.getValueNode();
                    Node valueNode = mergeNodes(baseInnerNode, templateInnerNode, pushMergeContext(context, baseKey));
                    baseNode.getValue().remove(baseTupleMap.get(baseKey));
                    baseNode.getValue().add(new NodeTuple(keyNode, valueNode));
                }
            }
        }
        return baseNode;
    }

    private Class<?> pushMergeContext(Class<?> context, String key)
    {
        if (context.equals(Resource.class) && isAction(key))
        {
            return Action.class;
        }
        return Object.class;
    }

    private Node mergeNodes(Node baseNode, Node templateNode, Class<?> context)
    {
        if (baseNode.getNodeId() == mapping && templateNode.getNodeId() == mapping)
        {
            return mergeMappingNodes((MappingNode) baseNode, (MappingNode) templateNode, context);
        }
        if (templateNode.getNodeId() == mapping)
        {
            return cleanMergedTuples((MappingNode) templateNode, context);
        }
        return baseNode;
    }

    private MappingNode cleanMergedTuples(MappingNode templateNode, Class<?> context)
    {

        List<NodeTuple> tuples = new ArrayList(templateNode.getValue());
        for (NodeTuple tuple : tuples)
        {
            String key = ((ScalarNode) tuple.getKeyNode()).getValue();
            if (nonMergeableFields(context).contains(key))
            {
                templateNode.getValue().remove(tuple);
            }
        }
        return templateNode;
    }

    private Set nonMergeableFields(Class<?> element)
    {
        String[] fields = {};
        if (element.equals(Resource.class))
        {
            fields = new String[] {"description", "summary", "displayName", "type", "is"};
        }
        else if (element.equals(Action.class))
        {
            fields = new String[] {"description", "summary", "displayName", "is"};
        }
        return new HashSet(Arrays.asList(fields));
    }

    private boolean isAction(String key)
    {
        try
        {
            ActionType.valueOf(normalizeKey(key).toUpperCase());
            return true;
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }

    }

    private boolean isOptional(String key)
    {
        return key.endsWith(OPTIONAL_MODIFIER);
    }

    private String getMatchingKey(Map<String, NodeTuple> tupleMap, String key)
    {
        key = normalizeKey(key);
        for (String resourceKey : tupleMap.keySet())
        {
            if (normalizeKey(resourceKey).equals(key))
            {
                return resourceKey;
            }
        }
        return null;
    }

    private String normalizeKey(String key)
    {
        if (key.endsWith(OPTIONAL_MODIFIER))
        {
            return key.substring(0, key.length() - 1);
        }
        return key;
    }

    private Map<String, NodeTuple> getTupleMap(MappingNode mappingNode)
    {
        Map<String, NodeTuple> tupleMap = new HashMap<String, NodeTuple>();
        for (NodeTuple tuple : mappingNode.getValue())
        {
            tupleMap.put(((ScalarNode) tuple.getKeyNode()).getValue(), tuple);
        }
        return tupleMap;
    }

    @Override
    protected void preBuildProcess()
    {
        buildTemplateMap(getRootNode());
    }

    private void buildTemplateMap(MappingNode rootNode)
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
                    templateSequence = includeResolver.resolveInclude((ScalarNode) templateSequence, getResourceLoader(), this);
                    rootNode.getValue().remove(i);
                    rootNode.getValue().add(i, new NodeTuple(rootTuple.getKeyNode(), templateSequence));
                }

                SequenceNode sequence = (SequenceNode) templateSequence;
                for (int j = 0; j < sequence.getValue().size(); j++)
                {
                    Node template = sequence.getValue().get(j);
                    if (template.getNodeId() == NodeId.scalar)
                    {
                        if (!template.getTag().startsWith(INCLUDE_TAG))
                        {
                            throw new RuntimeException("Mapping or !include expected: " + templateSequence.getStartMark());
                        }
                        template = includeResolver.resolveInclude((ScalarNode) template, getResourceLoader(), this);
                        sequence.getValue().remove(j);
                        sequence.getValue().add(j, template);
                    }
                    for (NodeTuple tuple : ((MappingNode) template).getValue())
                    {
                        String templateKey = ((ScalarNode) tuple.getKeyNode()).getValue();
                        MappingNode templateNode = (MappingNode) tuple.getValueNode();
                        if (key.equals("resourceTypes"))
                        {
                            resourceTypesMap.put(templateKey, templateNode);
                            logger.info("adding resource type: " + templateKey);
                        }
                        if (key.equals("traits"))
                        {
                            traitsMap.put(templateKey, templateNode);
                            logger.info("adding trait: " + templateKey);
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void postBuildProcess()
    {
    }
}
