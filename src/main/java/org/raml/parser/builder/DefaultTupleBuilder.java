package org.raml.parser.builder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Parent;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class DefaultTupleBuilder<K extends Node, V extends Node> implements TupleBuilder<K, V>
{

    protected Map<String, TupleBuilder<?, ?>> builders;
    private NodeBuilder<?> parent;
    private TupleHandler handler;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultTupleBuilder(TupleHandler tupleHandler)
    {
        builders = new HashMap<String, TupleBuilder<?, ?>>();
        this.setHandler(tupleHandler);
    }

    @Override
    public NodeBuilder getBuilderForTuple(NodeTuple tuple)
    {
        if (builders == null || builders.isEmpty())
        {
            return new DefaultTupleBuilder(new DefaultTupleHandler());
        }
        for (TupleBuilder tupleBuilder : builders.values())
        {
            if (tupleBuilder.getHandler().handles(tuple))
            {
                return tupleBuilder;
            }
        }
        throw new RuntimeException("Builder not found for " + tuple);
    }

    @Override
    public Object buildValue(Object parent, V node)
    {
        return parent;
    }

    public void setHandler(TupleHandler handler)
    {
        this.handler = handler;
    }

    @Override
    public TupleHandler getHandler()
    {
        return handler;
    }

    @Override
    public void buildKey(Object parent, K tuple)
    {

    }

    @Override
    public void setParentNodeBuilder(NodeBuilder parentBuilder)
    {
        parent = parentBuilder;
    }

    @Override
    public void setNestedBuilders(Map<String, TupleBuilder<?, ?>> nestedBuilders)
    {
        builders = nestedBuilders;
    }


    public void addBuildersFor(Class<?> documentClass)
    {
        new TupleBuilderFactory().addBuildersTo(documentClass, this);
    }


    public NodeBuilder getParent()
    {
        return parent;
    }

    protected void processPojoAnnotations(Object pojo, Object keyFieldName, Object parent)
    {
        List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojo.getClass());
        for (Field declaredField : declaredFields)
        {
            Key keyAnnotation = declaredField.getAnnotation(Key.class);
            Parent parentAnnotation = declaredField.getAnnotation(Parent.class);
            if (keyAnnotation != null)
            {
                ReflectionUtils.setProperty(pojo, declaredField.getName(), keyFieldName);
            }
            if (parentAnnotation != null)
            {
                Object value = parent;
                if (!parentAnnotation.property().isEmpty())
                {
                    try
                    {
                        value = PropertyUtils.getProperty(parent, parentAnnotation.property());
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
                }
                ReflectionUtils.setProperty(pojo, declaredField.getName(), value);
            }
        }
    }
}
