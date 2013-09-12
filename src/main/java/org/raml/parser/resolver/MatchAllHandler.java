package org.raml.parser.resolver;

import org.yaml.snakeyaml.nodes.NodeTuple;

public class MatchAllHandler implements TupleHandler
{

    @Override
    public boolean handles(NodeTuple tuple)
    {
        return true;
    }
}
