package org.raml.parser.visitor;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class CompositeHandler implements NodeHandler
{

    private NodeHandler[] nodeHandlers;

    public CompositeHandler(NodeHandler... nodeHandlers)
    {
        this.nodeHandlers = nodeHandlers;
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onMappingNodeStart(mappingNode);
        }
    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onMappingNodeEnd(mappingNode);
        }
    }

    @Override
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onSequenceStart(node, tupleType);
        }
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onSequenceEnd(node, tupleType);
        }
    }

    @Override
    public void onScalar(ScalarNode node, TupleType tupleType)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onScalar(node, tupleType);
        }
    }

    @Override
    public void onDocumentStart(MappingNode node)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onDocumentStart(node);
        }
    }

    @Override
    public void onDocumentEnd(MappingNode node)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onDocumentEnd(node);
        }
    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onTupleEnd(nodeTuple);
        }
    }

    @Override
    public void onTupleStart(NodeTuple nodeTuple)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onTupleStart(nodeTuple);
        }
    }

    @Override
    public void onSequenceElementStart(Node sequenceNode)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onSequenceElementStart(sequenceNode);
        }
    }

    @Override
    public void onSequenceElementEnd(Node sequenceNode)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onSequenceElementEnd(sequenceNode);
        }
    }

    @Override
    public void onIncludeResourceNotFound(ScalarNode node)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onIncludeResourceNotFound(node);
        }
    }

    @Override
    public void onIncludeStart(String includeName)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onIncludeStart(includeName);
        }
    }

    @Override
    public void onIncludeEnd(String includeName)
    {
        for (NodeHandler nh : nodeHandlers)
        {
            nh.onIncludeEnd(includeName);
        }
    }

}
