package org.raml.parser.builder;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class ScalarTupleBuilder extends DefaultTupleBuilder<ScalarNode, ScalarNode>
{

    private String fieldName;
    private Class<?> type;


    public ScalarTupleBuilder(String field, Class<?> type)
    {
        super(new DefaultScalarTupleHandler(ScalarNode.class, field));
        this.type = type;

    }


    @Override
    public Object buildValue(Object parent, ScalarNode node)
    {

        final String value = node.getValue();
        final Object converted = ConvertUtils.convertTo(value, type);
        String unalias = unalias(parent, fieldName);
        ReflectionUtils.setProperty(parent, unalias, converted);

        return parent;
    }

    @Override
    public void buildKey(Object parent, ScalarNode tuple)
    {
        fieldName = tuple.getValue();
    }
}
