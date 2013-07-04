package org.raml.parser.builder;

import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V> , TupleHandler
{

    /**
     * Returns the
     * @param tuple
     * @return
     */
    NodeBuilder getBuiderForTuple(NodeTuple tuple);

    void buildKey(Object parent, K tuple);

    void setHandler(TupleHandler handler);

    void setNestedBuilders(Map<String, TupleBuilder<?,?>> nestedBuilders);

}
