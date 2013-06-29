package org.raml.parser.builder;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V>
{

    NodeBuilder getBuiderForTuple(NodeTuple tuple);

    void buildKey(Object parent, K tuple);

}
