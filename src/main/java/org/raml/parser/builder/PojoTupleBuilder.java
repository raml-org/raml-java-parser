package org.raml.parser.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Value;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.raml.parser.utils.NodeUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class PojoTupleBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private Class<?> pojoClass;
    private String fieldName;

    public PojoTupleBuilder(String fieldName, Class<?> pojoClass)
    {
        super(new DefaultScalarTupleHandler(MappingNode.class, fieldName));
        this.fieldName = fieldName;
        this.pojoClass = pojoClass;

    }

    public PojoTupleBuilder(Class<?> pojoClass)
    {
        this(null, pojoClass);
    }

    @Override
    public NodeBuilder getBuilderForTuple(NodeTuple tuple)
    {
        if (builders.isEmpty())     //Do it lazzy so it support recursive structures
        {
            addBuildersFor(pojoClass);
        }
        return super.getBuilderForTuple(tuple);
    }


    @Override
    public Object buildValue(Object parent, Node node)
    {
        try
        {
            Object newValue;
            if (pojoClass.isEnum())
            {
                newValue = ConvertUtils.convertTo((String) NodeUtils.getNodeValue(node), pojoClass);
            }
            else if (pojoClass.getDeclaredConstructors().length > 0)
            {
                List<Object> arguments = new ArrayList<Object>();
                Constructor<?> declaredConstructor = pojoClass.getDeclaredConstructors()[0];
                Annotation[][] parameterAnnotations = declaredConstructor.getParameterAnnotations();
                for (Annotation[] parameterAnnotation : parameterAnnotations)
                {

                    if (parameterAnnotation[0].annotationType().equals(Value.class))
                    {
                        arguments.add(NodeUtils.getNodeValue(node));
                    }
                    else if (parameterAnnotation[0].annotationType().equals(Key.class))
                    {
                        arguments.add(fieldName);
                    }

                }

                newValue = declaredConstructor.newInstance(arguments.toArray(new Object[arguments.size()]));
            }
            else
            {
                newValue = pojoClass.newInstance();
            }
            ReflectionUtils.setProperty(parent, fieldName, newValue);
            processPojoAnnotations(newValue, fieldName, parent);
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
        catch (InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void buildKey(Object parent, ScalarNode node)
    {
        fieldName = node.getValue();
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public Class<?> getPojoClass()
    {
        return pojoClass;
    }

    public String toString()
    {
        return fieldName;
    }
}
