package org.raml.parser.rule;

import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private final Class valueType;
    private String fieldName;

    public MapTupleRule(String fieldName, Class valueType)
    {
        super(fieldName, new DefaultScalarTupleHandler(Node.class, fieldName));
        this.valueType = valueType;

    }

    public MapTupleRule(Class<?> valueType, NodeRuleFactory nodeRuleFactory)
    {
        this(null, valueType);
        setNodeRuleFactory(nodeRuleFactory);
    }


    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        //TODO add it to a list to invoke onRuleEnd on all the rules created
        return new PojoTupleRule(fieldName, valueType, getNodeRuleFactory());
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        fieldName = key.getValue();
        return super.validateKey(key);
    }
}
