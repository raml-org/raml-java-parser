package org.raml.parser.builder;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;


public class SequenceTupleBuilder extends DefaultTupleBuilder<Node, SequenceNode> implements SequenceBuilder
{


    private String fieldName;
    private Class<?> elementClass;

    public SequenceTupleBuilder(String fieldName, Class<?> elementClass)
    {
        super(new DefaultScalarTupleHandler(SequenceNode.class, fieldName));
        this.elementClass = elementClass;
        this.fieldName = fieldName;

    }

    @Override
    public Object buildValue(Object parent, SequenceNode tuple)
    {
        List<?> list = new ArrayList();
        ReflectionUtils.setProperty(parent, fieldName, list);
        return list;
    }

    @Override
    public NodeBuilder getItemBuilder()
    {
        return new PojoTupleBuilder(elementClass);
    }


}
