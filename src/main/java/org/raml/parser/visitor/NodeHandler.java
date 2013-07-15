package org.raml.parser.visitor;

import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public interface NodeHandler extends ResourceLoader
{

    void onMappingNodeStart(MappingNode mappingNode);

    void onMappingNodeEnd(MappingNode mappingNode);

    void onSequenceStart(SequenceNode node, TupleType tupleType);

    void onSequenceEnd(SequenceNode node, TupleType tupleType);

    void onScalar(ScalarNode node, TupleType tupleType);

    void onDocumentStart(MappingNode node);

    void onDocumentEnd(MappingNode node);

    void onTupleEnd(NodeTuple nodeTuple);

    void onTupleStart(NodeTuple nodeTuple);

}
