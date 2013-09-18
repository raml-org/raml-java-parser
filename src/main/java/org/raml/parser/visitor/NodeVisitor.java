package org.raml.parser.visitor;

import static org.raml.parser.visitor.IncludeResolver.INCLUDE_TAG;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class NodeVisitor
{

    private NodeHandler nodeHandler;
    private ResourceLoader resourceLoader;
    private IncludeResolver includeResolver = new IncludeResolver();

    public NodeVisitor(NodeHandler nodeHandler, ResourceLoader resourceLoader)
    {
        super();
        this.nodeHandler = nodeHandler;
        this.resourceLoader = resourceLoader;
    }

    private void visitMappingNode(MappingNode mappingNode, TupleType tupleType)
    {
        nodeHandler.onMappingNodeStart(mappingNode);
        doVisitMappingNode(mappingNode);
        nodeHandler.onMappingNodeEnd(mappingNode);
    }

    private void doVisitMappingNode(MappingNode mappingNode)
    {
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
            String includeName = null;
            if (valueNode.getTag().startsWith(INCLUDE_TAG))
            {
                includeName = ((ScalarNode) valueNode).getValue();
                valueNode = includeResolver.resolveInclude((ScalarNode) valueNode, resourceLoader, nodeHandler);
                nodeTuple = new NodeTuple(keyNode, valueNode);
            }
            updatedTuples.add(nodeTuple);
            if (includeName != null)
            {
                nodeHandler.onIncludeStart(includeName);
            }
            nodeHandler.onTupleStart(nodeTuple);
            visit(keyNode, TupleType.KEY);
            visit(valueNode, TupleType.VALUE);
            nodeHandler.onTupleEnd(nodeTuple);
            if (includeName != null)
            {
                nodeHandler.onIncludeEnd(includeName);
            }
        }
        mappingNode.setValue(updatedTuples);
    }

    public void visitDocument(MappingNode node)
    {
        nodeHandler.onDocumentStart(node);
        if (node instanceof MappingNode)
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
