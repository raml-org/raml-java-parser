package org.raml.parser.rule;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class GlobalSchemasRule extends SequenceTupleRule
{

    private Map<String, String> schemas = new HashMap<String, String>();

    public GlobalSchemasRule()
    {
        super("schemas", null);
    }

    @Override
    public NodeRule<?> getItemRule()
    {
        MapTupleRule mapTupleRule = new GlobalSchemaTupleRule(String.class, getNodeRuleFactory());
        return mapTupleRule;
    }

    private class GlobalSchemaTupleRule extends MapTupleRule
    {

        public GlobalSchemaTupleRule(Class<String> valueType, NodeRuleFactory nodeRuleFactory)
        {
            super(valueType, nodeRuleFactory);
        }

        @Override
        public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
        {
            String schemaKey = ((ScalarNode) nodeTuple.getKeyNode()).getValue();
            String schemaValue = ((ScalarNode) nodeTuple.getValueNode()).getValue();
            schemas.put(schemaKey, schemaValue);
            return super.getRuleForTuple(nodeTuple);
        }
    }

    public Map<String, String> getSchemas()
    {
        return schemas;
    }
}
