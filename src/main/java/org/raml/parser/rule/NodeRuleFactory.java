/**
 *
 */
package org.raml.parser.rule;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.builder.AbastractFactory;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.resolver.EnumHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class NodeRuleFactory extends AbastractFactory
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private NodeRuleFactoryExtension[] extensions;


    public NodeRuleFactory(NodeRuleFactoryExtension... extensions)
    {
        this.extensions = extensions;
    }

    public DefaultTupleRule<Node, MappingNode> createDocumentRule(Class<?> documentClass)
    {
        DefaultTupleRule<Node, MappingNode> documentRule = new DefaultTupleRule<Node, MappingNode>(null, new DefaultTupleHandler());
        documentRule.setNodeRuleFactory(this);
        documentRule.addRulesFor(documentClass);
        return documentRule;
    }


    public void addRulesTo(Class<?> pojoClass, TupleRule<?, ?> parent)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("adding rules for " + pojoClass);
        }
        final List<Field> declaredFields = ReflectionUtils.getInheritedFields(pojoClass);
        final Map<String, TupleRule<?, ?>> innerBuilders = new HashMap<String, TupleRule<?, ?>>();
        for (Field declaredField : declaredFields)
        {
            Scalar scalar = declaredField.getAnnotation(Scalar.class);
            Mapping mapping = declaredField.getAnnotation(Mapping.class);
            Sequence sequence = declaredField.getAnnotation(Sequence.class);
            TupleRule<?, ?> tupleRule = null;
            TupleHandler tupleHandler = null;
            boolean required = false;
            if (scalar != null)
            {
                tupleRule = createScalarRule(declaredField, scalar);
                tupleHandler = createHandler(scalar.handler(), scalar.alias(), ScalarNode.class);
                required = scalar.required();
            }
            else if (mapping != null)
            {
                tupleRule = createMappingRule(declaredField, mapping);
                tupleHandler = createHandler(mapping.handler(), mapping.alias(), MappingNode.class);
                required = mapping.required();
            }
            else if (sequence != null)
            {
                tupleRule = createSequenceRule(declaredField, sequence);
                tupleHandler = createHandler(sequence.handler(), sequence.alias(), SequenceNode.class);
                required = sequence.required();
            }

            if (tupleRule != null)
            {
                if (tupleHandler != null)
                {
                    tupleRule.setHandler(tupleHandler);
                }
                tupleRule.setRequired(required);
                tupleRule.setParentTupleRule(parent);
                tupleRule.setNodeRuleFactory(this);
                innerBuilders.put(declaredField.getName(), tupleRule);
            }
        }
        parent.setNestedRules(innerBuilders);
    }

    private TupleRule<?, ?> createSequenceRule(Field declaredField, Sequence sequence)
    {
        TupleRule<?, ?> tupleRule = null;
        if (sequence.rule() != TupleRule.class)
        {
            tupleRule = createInstanceOf(sequence.rule());
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
                        tupleRule = new SequenceTupleRule(declaredField.getName(), (Class<?>) itemType);
                    }
                }
            }
            else
            {
                throw new RuntimeException("Only List can be sequence. Error on field " + declaredField.getName());
            }

        }
        return tupleRule;
    }

    private TupleRule<?, ?> createMappingRule(Field declaredField, Mapping mapping)
    {
        TupleRule<?, ?> tupleRule = null;
        if (mapping.rule() != TupleRule.class)
        {
            tupleRule = createInstanceOf(mapping.rule());
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
                            tupleRule = new ImplicitMapEntryRule(declaredField.getName(), (Class) valueType);
                        }
                        else
                        {
                            tupleRule = new MapTupleRule(declaredField.getName(), (Class) valueType);
                        }
                        if (keyClass.isEnum())
                        {
                            tupleRule.setHandler(new EnumHandler(MappingNode.class, (Class<? extends Enum>) keyClass));
                        }
                    }
                }
            }
            else
            {
                tupleRule = new PojoTupleRule(declaredField.getName(), declaredField.getType());
            }
        }
        List<TupleRule> contributionRules = new ArrayList<TupleRule>();
        for (NodeRuleFactoryExtension extension : extensions)
        {
            if (extension.handles(declaredField, mapping))
            {
                TupleRule<?, ?> rule = extension.createRule(declaredField, mapping);
                contributionRules.add(rule);
            }
        }
        if (!contributionRules.isEmpty())
        {
            tupleRule = new ContributionTupleRule(tupleRule, contributionRules);
        }

        return tupleRule;
    }

    private TupleRule<?, ?> createScalarRule(Field declaredField, Scalar scalar)
    {
        TupleRule<?, ?> tupleRule;
        if (scalar.rule() != TupleRule.class)
        {
            tupleRule = createInstanceOf(scalar.rule());
        }
        else
        {
            tupleRule = new SimpleRule(declaredField.getName(), declaredField.getType());
        }
        return tupleRule;
    }
}
