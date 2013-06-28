package heaven.parser.builder;

import java.util.Map;

import heaven.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: santiagovacas
 * Date: 6/28/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NodeBuilder<V extends Node> extends TupleHandler
{

    Object buildValue(Object parent, V tuple);

    void setParentNodeBuilder(NodeBuilder parentBuilder);

    void setNestedBuilders(Map<String, NodeBuilder<?>> nestedBuilders);

    void setHandler(TupleHandler handler);
}
