/**
 *
 */
package org.raml.parser.builder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.EnumHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class TupleBuilderFactory extends AbastractFactory
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    public void addBuildersTo(Class<?> pojoClass, TupleBuilder parent)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("adding builders for " + pojoClass);
        }
        final List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojoClass);
        final Map<String, TupleBuilder<?, ?>> innerBuilders = new HashMap<String, TupleBuilder<?, ?>>();
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            TupleBuilder<?, ?> tupleBuilder = null;
            TupleHandler tupleHandler = null;
            if (scalar != null)
            {
                tupleBuilder = createScalarBuilder(declaredField, scalar);
                tupleHandler = createHandler(scalar.handler(), scalar.alias(), ScalarNode.class);

            }
            else if (mapping != null)
            {
                tupleBuilder = createMappingBuilder(declaredField, mapping);
                tupleHandler = createHandler(mapping.handler(), mapping.alias(), MappingNode.class);
            }
            else if (sequence != null)
            {
                tupleBuilder = createSequenceBuilder(declaredField, sequence);
                tupleHandler = createHandler(sequence.handler(), sequence.alias(), SequenceNode.class);
            }

            if (tupleBuilder != null)
            {
                if (tupleHandler != null)
                {
                    tupleBuilder.setHandler(tupleHandler);
                }
                tupleBuilder.setParentNodeBuilder(parent);
                innerBuilders.put(declaredField.getName(), tupleBuilder);
            }
        }
        parent.setNestedBuilders(innerBuilders);
    }

    private TupleBuilder<?, ?> createSequenceBuilder(Field declaredField, Sequence sequence)
    {
        TupleBuilder<?, ?> tupleBuilder = null;
        if (sequence.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(sequence.builder());
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
        return tupleBuilder;
    }

    private TupleBuilder<?, ?> createScalarBuilder(Field declaredField, Scalar scalar)
    {
        TupleBuilder<?, ?> tupleBuilder;
        if (scalar.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(scalar.builder());
        }
        else
        {
            tupleBuilder = new ScalarTupleBuilder(declaredField.getName(), declaredField.getType());
        }
        return tupleBuilder;
    }

    private TupleBuilder<?, ?> createMappingBuilder(Field declaredField, Mapping mapping)
    {
        TupleBuilder<?, ?> tupleBuilder = null;
        if (mapping.builder() != TupleBuilder.class)
        {
            tupleBuilder = createInstanceOf(mapping.builder());
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
        return tupleBuilder;
    }


}
