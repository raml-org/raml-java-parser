package org.raml.parser.rule;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class PojoTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{


    private Class<?> pojoClass;

    public PojoTupleRule(String fieldName, Class<?> pojoClass, NodeRuleFactory nodeRuleFactory)
    {
        this(fieldName, pojoClass);
        setNodeRuleFactory(nodeRuleFactory);
    }

    public PojoTupleRule(String fieldName, Class<?> pojoClass)
    {
        super(fieldName, new DefaultScalarTupleHandler(MappingNode.class, fieldName));
        this.pojoClass = pojoClass;
    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        if (rules.isEmpty())
        {
            addRulesFor(pojoClass);
        }
        return super.getRuleForTuple(nodeTuple);
    }
}
