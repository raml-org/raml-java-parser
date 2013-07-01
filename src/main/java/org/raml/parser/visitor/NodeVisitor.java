package org.raml.parser.visitor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.yaml.snakeyaml.Yaml;
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

    public NodeVisitor(NodeHandler nodeHandler)
    {
        super();
        this.nodeHandler = nodeHandler;
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
            Node valueNode = nodeTuple.getValueNode();
            if (valueNode.getTag().startsWith("tag:raml.org,0.1:include"))
            {
                valueNode = resolveInclude((ScalarNode) valueNode);
                nodeTuple = new NodeTuple(keyNode, valueNode);
            }
            updatedTuples.add(nodeTuple);
            nodeHandler.onTupleStart(nodeTuple);
            visit(keyNode, TupleType.KEY);
            visit(valueNode, TupleType.VALUE);
            nodeHandler.onTupleEnd(nodeTuple);
        }
        mappingNode.setValue(updatedTuples);
    }

    public void visitDocument(MappingNode node)
    {
        nodeHandler.onDocumentStart(node);
        doVisitMappingNode(node);
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
        for (Node node2 : value)
        {
            visit(node2, tupleType);
        }
        nodeHandler.onSequenceEnd(node, tupleType);
    }

    private void visitScalar(ScalarNode node, TupleType tupleType)
    {
        nodeHandler.onScalar(node, tupleType);
    }

    private Node resolveInclude(ScalarNode node)
    {
        Node includeNode;
        InputStream inputStream = null;
        try
        {
            String resourceName = node.getValue();
            try
            {
                URL url = new URL(resourceName);
                inputStream = new BufferedInputStream(url.openStream());
            }
            catch (MalformedURLException e)
            {
                inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
            }

            if (inputStream == null)
            {
                throw new RuntimeException("resource not found: " + resourceName);
            }
            if (resourceName.endsWith(".yaml") || resourceName.endsWith(".yml"))
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
