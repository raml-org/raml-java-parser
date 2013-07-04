/**
 *
 */
package org.raml.parser.builder;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;

public class AbastractFactory
{

    protected TupleHandler createHandler(Class<? extends TupleHandler> handler, String alias,
                                       Class<? extends Node> nodeClass)
    {
        TupleHandler tupleHandler = null;
        if (handler != TupleHandler.class)
        {
            tupleHandler = createInstanceOf(handler);
        }
        else if (!alias.isEmpty())
        {
            tupleHandler = new DefaultScalarTupleHandler(nodeClass, alias);
        }
        return tupleHandler;
    }

    protected <T> T createInstanceOf(Class<? extends T> handler)
    {
        try
        {
            return handler.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
