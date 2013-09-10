package org.raml.parser.builder;

import java.util.ArrayList;

import org.raml.model.parameter.UriParameter;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class MapWithListValueTupleBuilder extends MapTupleBuilder
{


    public MapWithListValueTupleBuilder(String fieldName, Class<UriParameter> valueClass)
    {
        super(fieldName, valueClass);
    }

    @Override
    public TupleBuilder getBuilderForTuple(NodeTuple tuple)
    {
        final String fieldName = ((ScalarNode) tuple.getKeyNode()).getValue();
        if (tuple.getValueNode() instanceof SequenceNode)
        {

            return new SequenceTupleBuilder(fieldName, getValueClass());
        }
        else
        {
            return new ListOfPojoTupleBuilder(fieldName, getValueClass());
        }
    }

    //Hack class to make non sequence mapping node act as sequence
    private static class ListOfPojoTupleBuilder extends PojoTupleBuilder
    {

        public ListOfPojoTupleBuilder(String fieldName, Class<?> pojoClass)
        {
            super(fieldName, pojoClass);
        }

        @Override
        public Object buildValue(Object parent, Node node)
        {
            try
            {
                Object newValue = getPojoClass().newInstance();
                ArrayList<Object> objects = new ArrayList<Object>();
                objects.add(newValue);
                ReflectionUtils.setProperty(parent, getFieldName(), objects);
                processPojoAnnotations(newValue, getFieldName(), parent);
                return newValue;
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

}
