package org.raml.parser.resolver;

import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class EnumHandler implements TupleHandler
{


    private Class<? extends Enum> enumClass;

    public EnumHandler(Class<? extends Node> tupleValueType, Class<? extends Enum> enumClass)
    {
        this.enumClass = enumClass;
    }

    @Override
    public boolean handles(NodeTuple tuple)
    {
        if (tuple.getKeyNode() instanceof ScalarNode)
        {
            String enumValue = ((ScalarNode) tuple.getKeyNode()).getValue();
            try
            {
                Enum anEnum = Enum.valueOf(enumClass, enumValue.toUpperCase());
            }
            catch (IllegalArgumentException e)
            {
                return false;
            }
            return true;
        }
        return false;
    }
}
