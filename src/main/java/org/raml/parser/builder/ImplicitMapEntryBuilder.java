package org.raml.parser.builder;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ImplicitMapEntryBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private String fieldName;

    private String keyValue;
    private Class<?> keyClass;
    private Class valueClass;


    public ImplicitMapEntryBuilder(String fieldName, Class<?> keyClass, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(Node.class, fieldName));
        this.fieldName = fieldName;
        this.keyClass = keyClass;
        this.valueClass = valueClass;
        addBuildersFor(valueClass);
    }

    @Override
    public Object buildValue(Object parent, Node tuple)
    {

        Map actualParent;
        try
        {
            actualParent = (Map) new PropertyUtilsBean().getProperty(parent, fieldName);
            Object newValue = valueClass.newInstance();
            actualParent.put(ConvertUtils.convertTo(keyValue, keyClass), newValue);
            return newValue;
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
        catch (NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void buildKey(Object parent, ScalarNode tuple)
    {
        keyValue = tuple.getValue();
    }
}
