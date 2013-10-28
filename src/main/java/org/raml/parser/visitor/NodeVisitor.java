package org.raml.parser.visitor;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class NodeVisitor
{

    private NodeHandler nodeHandler;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    public NodeVisitor(NodeHandler nodeHandler, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        super();
        this.nodeHandler = nodeHandler;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
    }

    private void visitMappingNode(MappingNode mappingNode, TupleType tupleType)
    {
        nodeHandler.onMappingNodeStart(mappingNode);
        doVisitMappingNode(mappingNode);
        nodeHandler.onMappingNodeEnd(mappingNode);
    }

    private class MappingNodeMerger extends SafeConstructor
    {
        void merge(MappingNode mappingNode)
        {
            flattenMapping(mappingNode);
        }
    }

    private void doVisitMappingNode(MappingNode mappingNode)
    {
        if (mappingNode.isMerged())
        {
            new MappingNodeMerger().merge(mappingNode);
        }
        List<NodeTuple> tuples = mappingNode.getValue();
        List<NodeTuple> updatedTuples = new ArrayList<NodeTuple>();
        for (NodeTuple nodeTuple : tuples)
        {
            Node keyNode = nodeTuple.getKeyNode();
            if (!(keyNode instanceof ScalarNode))
            {
                throw new YAMLException("Only scalar keys are allowed: " + keyNode.getStartMark());
            }
            Node valueNode = nodeTuple.getValueNode();
            Node originalValueNode = valueNode;
            Tag tag = valueNode.getTag();
            TagResolver tagResolver = getTagResolver(tag);
            if (tagResolver != null)
            {
                valueNode = tagResolver.resolve(valueNode, resourceLoader, nodeHandler);
                nodeTuple = new NodeTuple(keyNode, valueNode);
            }
            updatedTuples.add(nodeTuple);
            if (tagResolver != null)
            {
                nodeHandler.onCustomTagStart(tag, originalValueNode, nodeTuple);
            }
            nodeHandler.onTupleStart(nodeTuple);
            visit(keyNode, TupleType.KEY);
            visit(valueNode, TupleType.VALUE);
            nodeHandler.onTupleEnd(nodeTuple);
            if (tagResolver != null)
            {
                nodeHandler.onCustomTagEnd(tag, originalValueNode, nodeTuple);
            }

        }
        mappingNode.setValue(updatedTuples);
    }

    private TagResolver getTagResolver(Tag tag)
    {
        for (TagResolver resolver : tagResolvers)
        {
            if (resolver.handles(tag))
            {
                return resolver;
            }
        }
        return null;
    }

    public void visitDocument(MappingNode node)
    {
        nodeHandler.onDocumentStart(node);
        if (node != null)
        {
            doVisitMappingNode(node);
        }
        nodeHandler.onDocumentEnd(node);
    }

    private void visit(Node node, TupleType tupleType)
    {
        if (node.getNodeId() == NodeId.mapping)
        {
            visitMappingNode((MappingNode) node, tupleType);
        }
        else if (node.getNodeId() == NodeId.scalar)
        {
            visitScalar((ScalarNode) node, tupleType);
        }
        else if (node.getNodeId() == NodeId.sequence)
        {
            visitSequence((SequenceNode) node, tupleType);
        }
    }

    private void visitSequence(SequenceNode node, TupleType tupleType)
    {
        nodeHandler.onSequenceStart(node, tupleType);
        List<Node> value = node.getValue();
        for (Node sequenceNode : value)
        {
            nodeHandler.onSequenceElementStart(sequenceNode);
            visit(sequenceNode, tupleType);
            nodeHandler.onSequenceElementEnd(sequenceNode);
        }
        nodeHandler.onSequenceEnd(node, tupleType);
    }

    private void visitScalar(ScalarNode node, TupleType tupleType)
    {
        nodeHandler.onScalar(node, tupleType);
    }


}
