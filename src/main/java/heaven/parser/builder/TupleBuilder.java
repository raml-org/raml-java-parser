package heaven.parser.builder;

import java.util.Map;

import heaven.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V>
{

    NodeBuilder getBuiderForTuple(NodeTuple tuple);

    void buildKey(Object parent, K tuple);

}
