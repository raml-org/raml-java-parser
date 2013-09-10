package org.raml.parser.builder;

import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V>
{

    /**
     * Returns the
     * @param tuple
     * @return
     */
    NodeBuilder getBuilderForTuple(NodeTuple tuple);

    void buildKey(Object parent, K tuple);

    void setHandler(TupleHandler handler);

    TupleHandler getHandler();

    void setNestedBuilders(Map<String, TupleBuilder<?,?>> nestedBuilders);

}
