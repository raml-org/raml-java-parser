package org.raml.parser.builder;

import java.util.HashMap;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleBuilder extends DefaultTupleBuilder<ScalarNode, MappingNode>
{

    private Class valueClass;
    private String fieldName;

    public MapTupleBuilder(Class<?> valueClass)
    {
        this(null, valueClass);
    }

    public MapTupleBuilder(String fieldName, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(MappingNode.class, fieldName));
        this.fieldName = fieldName;
        this.valueClass = valueClass;
    }

    @Override
    public TupleBuilder getBuilderForTuple(NodeTuple tuple)
    {
        return new PojoTupleBuilder(getValueClass());
    }

    @Override
    public Object buildValue(Object parent, MappingNode node)
    {
        final HashMap<String, Object> map = new HashMap<String, Object>();
        ReflectionUtils.setProperty(parent, getFieldName(), map);
        return map;
    }


    public Class getValueClass()
    {
        return valueClass;
    }

    public String getFieldName()
    {
        return fieldName;
    }
}
