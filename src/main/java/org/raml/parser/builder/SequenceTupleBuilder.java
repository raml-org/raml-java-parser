package org.raml.parser.builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceTupleBuilder extends DefaultTupleBuilder<Node, SequenceNode> implements SequenceBuilder
{


    private String fieldName;
    private Type itemType;

    public SequenceTupleBuilder(String fieldName, Type itemType)
    {
        super(new DefaultScalarTupleHandler(SequenceNode.class, fieldName));
        this.itemType = itemType;
        this.fieldName = fieldName;
    }

    @Override
    public Object buildValue(Object parent, SequenceNode node)
    {
        List<?> list = new ArrayList();
        ReflectionUtils.setProperty(parent, fieldName, list);
        return list;
    }

    @Override
    public NodeBuilder getItemBuilder()
    {
        if (itemType instanceof Class<?>)
        {
            if (ReflectionUtils.isWrapperOrString((Class<?>) itemType))
            {
                //sequence of scalars
                return new ScalarTupleBuilder(fieldName, (Class<?>) itemType);
            }
            //sequence of pojos
            return new PojoTupleBuilder((Class<?>) itemType);
        }

        if (itemType instanceof ParameterizedType)
        {
            ParameterizedType pItemType = (ParameterizedType) itemType;
            if (Map.class.isAssignableFrom((Class<?>) pItemType.getRawType()))
            {
                //sequence of maps
                return new MapTupleBuilder((Class<?>) pItemType.getActualTypeArguments()[1]);
            }
        }
        throw new IllegalArgumentException("Sequence item type not supported: " + itemType);
    }

}
