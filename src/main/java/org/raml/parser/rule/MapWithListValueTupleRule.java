package org.raml.parser.rule;

import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapWithListValueTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private final Class valueType;
    private String fieldName;


    public MapWithListValueTupleRule(String fieldName, Class<?> valueType, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, new DefaultScalarTupleHandler(Node.class, fieldName), nodeRuleFactory);
        this.valueType = valueType;
    }


    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        if (nodeTuple.getValueNode().getNodeId() == NodeId.sequence)
        {
            return new SequenceTupleRule(fieldName, valueType, getNodeRuleFactory());
        }
        else
        {
            //TODO add it to a list to invoke onRuleEnd on all the rules created
            return new PojoTupleRule(fieldName, valueType, getNodeRuleFactory());
        }
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        fieldName = key.getValue();
        return super.validateKey(key);
    }
}
