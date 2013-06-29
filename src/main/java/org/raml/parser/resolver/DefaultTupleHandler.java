package org.raml.parser.resolver;

import org.yaml.snakeyaml.nodes.NodeTuple;

public class DefaultTupleHandler implements  TupleHandler
{

    @Override
    public boolean handles(NodeTuple tuple)
    {
        return false;
    }
}
