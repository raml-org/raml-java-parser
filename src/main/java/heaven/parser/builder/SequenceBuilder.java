package heaven.parser.builder;

import org.yaml.snakeyaml.nodes.SequenceNode;

/**
 * Created with IntelliJ IDEA.
 * User: santiagovacas
 * Date: 6/28/13
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SequenceBuilder
{

    NodeBuilder getItemBuilder();
}
