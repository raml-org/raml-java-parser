package org.raml.parser.builder;

import org.yaml.snakeyaml.nodes.Node;

/**
 * Created with IntelliJ IDEA.
 * User: santiagovacas
 * Date: 6/28/13
 * Time: 5:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface NodeBuilder<V extends Node>
{

    /**
     * Builds the java model for the given node and set it to the parent object
     *
     * @param parent The parent object
     * @param node   The node to build the model from
     * @return The model
     */
    Object buildValue(Object parent, V node);

    /**
     * Sets the parent builder
     * @param parentBuilder
     */
    void setParentNodeBuilder(NodeBuilder parentBuilder);

}
