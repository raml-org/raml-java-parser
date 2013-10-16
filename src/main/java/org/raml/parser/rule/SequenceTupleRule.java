package org.raml.parser.rule;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceTupleRule extends DefaultTupleRule<ScalarNode, SequenceNode> implements SequenceRule
{

    private Type itemType;

    public SequenceTupleRule(String fieldName, Type itemType)
    {
        this(fieldName, itemType, null);
    }

    public SequenceTupleRule(String fieldName, Type itemType, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, new DefaultScalarTupleHandler(SequenceNode.class, fieldName), nodeRuleFactory);
        this.itemType = itemType;

    }

    @Override
    public NodeRule<?> getItemRule()
    {
        if (itemType instanceof Class<?>)
        {
            //TODO add it to a list to invoke onRuleEnd on all the rules created
            if (ReflectionUtils.isWrapperOrString((Class) itemType))
            {
                return new SimpleRule(getName(), (Class<?>) itemType);
            }
            return new PojoTupleRule("", (Class<?>) itemType, getNodeRuleFactory());
        }

        if (itemType instanceof ParameterizedType)
        {
            ParameterizedType pItemType = (ParameterizedType) itemType;
            if (Map.class.isAssignableFrom((Class<?>) pItemType.getRawType()))
            {
                //sequence of maps
                return new MapTupleRule((Class<?>) pItemType.getActualTypeArguments()[1], getNodeRuleFactory());
            }
        }
        throw new IllegalArgumentException("Sequence item type not supported: " + itemType);
    }
}
