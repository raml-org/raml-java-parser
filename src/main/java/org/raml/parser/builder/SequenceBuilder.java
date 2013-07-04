package org.raml.parser.builder;

/**
 * Created with IntelliJ IDEA.
 * User: santiagovacas
 * Date: 6/28/13
 * Time: 5:50 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SequenceBuilder
{

    /**
     * Returns the builder for the items of this sequence
     *
     * @return
     */
    NodeBuilder getItemBuilder();
}
