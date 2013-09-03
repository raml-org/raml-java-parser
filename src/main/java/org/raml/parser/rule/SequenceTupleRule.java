package org.raml.parser.rule;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class SequenceTupleRule extends DefaultTupleRule<ScalarNode, SequenceNode> implements SequenceRule
{

    private Class<?> elementClass;

    public SequenceTupleRule(String fieldName, Class<?> elementClass)
    {
        super(fieldName, new DefaultScalarTupleHandler(SequenceNode.class, fieldName));
        this.elementClass = elementClass;
    }

    @Override
    public NodeRule<?> getItemRule()
    {
        //TODO add it to a list to invoke onRuleEnd on all the rules created
        if (ReflectionUtils.isWrapperOrString(elementClass))
        {
            return new SimpleRule(getName(), elementClass);
        }
        return new PojoTupleRule("", elementClass, getNodeRuleFactory());
    }
}
