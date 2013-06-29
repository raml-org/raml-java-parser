package org.raml.parser.builder;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.resolver.EnumHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class DefaultTupleBuilder<K extends Node, V extends Node> implements TupleBuilder<K, V>
{

    protected Map<String, NodeBuilder<?>> builders;
    private NodeBuilder<?> parent;
    private TupleHandler handler;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public DefaultTupleBuilder(TupleHandler tupleHandler)
    {
        builders = new HashMap<String, NodeBuilder<?>>();
        this.setHandler(tupleHandler);
    }

    @Override
    public NodeBuilder getBuiderForTuple(NodeTuple tuple)
    {
        if (builders == null || builders.isEmpty())
        {
            return new DefaultTupleBuilder(new DefaultTupleHandler());
        }
        for (NodeBuilder tupleBuilder : builders.values())
        {
            if (tupleBuilder.handles(tuple))
            {
                return tupleBuilder;
            }
        }
        throw new RuntimeException("Builder not found for " + tuple);
    }

    @Override
    public Object buildValue(Object parent, V tuple)
    {
        return parent;
    }

    public void setHandler(TupleHandler handler)
    {
        this.handler = handler;
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
    public void setNestedBuilders(Map<String, NodeBuilder<?>> nestedBuilders)
    {
        builders = nestedBuilders;
    }


    public void addBuildersFor(Class<?> documentClass)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("adding builders for " + documentClass);
        }
        List<Field> declaredFields = ReflectionUtils.getInheritedFields(documentClass);
        Map<String, NodeBuilder<?>> innerBuilders = new HashMap<String, NodeBuilder<?>>();
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            NodeBuilder tupleBuilder = null;
            TupleHandler tupleHandler = null;
            if (scalar != null)
            {

                if (scalar.builder() != TupleBuilder.class)
                {
                    tupleBuilder = createCustomBuilder(scalar.builder());
                }
                else
                {
                    tupleBuilder = new ScalarTupleBuilder(declaredField.getName(), declaredField.getType());
                }
                if (scalar.handler() != TupleHandler.class)
                {
                    tupleHandler = createHandler(scalar.handler());
                }

            }
            else if (mapping != null)
            {
                if (mapping.builder() != TupleBuilder.class)
                {
                    tupleBuilder = createCustomBuilder(mapping.builder());
                }
                else
                {
                    if (Map.class.isAssignableFrom(declaredField.getType()))
                    {
                        Type type = declaredField.getGenericType();
                        if (type instanceof ParameterizedType)
                        {
                            ParameterizedType pType = (ParameterizedType) type;
                            Type keyType = pType.getActualTypeArguments()[0];
                            Type valueType = pType.getActualTypeArguments()[1];
                            if (keyType instanceof Class<?> && valueType instanceof Class<?>)
                            {
                                Class<?> keyClass = (Class<?>) keyType;
                                if (mapping.implicit())
                                {
                                    tupleBuilder = new ImplicitMapEntryBuilder(declaredField.getName(), keyClass, (Class) valueType);
                                }
                                else
                                {
                                    tupleBuilder = new MapTupleBuilder(declaredField.getName(), keyClass, (Class) valueType);
                                }
                                if (keyClass.isEnum())
                                {
                                    tupleBuilder.setHandler(new EnumHandler(MappingNode.class, (Class<? extends Enum>) keyClass));
                                }
                            }
                        }
                    }
                    else
                    {
                        tupleBuilder = new PojoTupleBuilder(declaredField.getName(), declaredField.getDeclaringClass());
                    }
                }

                if (mapping.handler() != TupleHandler.class)
                {
                    tupleHandler = createHandler(mapping.handler());
                }
            }
            else if (sequence != null)
            {
                if (sequence.builder() != TupleBuilder.class)
                {
                    tupleBuilder = createCustomBuilder(sequence.builder());
                }
                else
                {
                    if (List.class.isAssignableFrom(declaredField.getType()))
                    {
                        Type type = declaredField.getGenericType();
                        if (type instanceof ParameterizedType)
                        {
                            ParameterizedType pType = (ParameterizedType) type;
                            Type itemType = pType.getActualTypeArguments()[0];
                            if (itemType instanceof Class<?>)
                            {
                                tupleBuilder = new SequenceTupleBuilder(declaredField.getName(), (Class<?>) itemType);
                            }
                        }
                    }
                    else
                    {
                        throw new RuntimeException("Only List can be sequence. Error on field " + declaredField.getName());
                    }
                }

                if (sequence.handler() != TupleHandler.class)
                {
                    tupleHandler = createHandler(sequence.handler());
                }
            }
            if (tupleBuilder != null)
            {
                if (tupleHandler != null)
                {
                    tupleBuilder.setHandler(tupleHandler);
                }
                tupleBuilder.setParentNodeBuilder(this);
                innerBuilders.put(declaredField.getName(), tupleBuilder);
            }
        }
        setNestedBuilders(innerBuilders);
    }

    private TupleHandler createHandler(Class<? extends TupleHandler> handler)
    {
        try
        {
            return handler.newInstance();
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


    private TupleBuilder createCustomBuilder(Class<? extends TupleBuilder> builder)
    {
        TupleBuilder tupleBuilder;
        try
        {
            tupleBuilder = builder.newInstance();
        }
        catch (InstantiationException e)
        {
            throw new RuntimeException(e);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
        return tupleBuilder;
    }

    @Override
    public final boolean handles(NodeTuple tuple)
    {
        return handler != null ? handler.handles(tuple) : false;
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
                if (parentAnnotation.property() != null)
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
