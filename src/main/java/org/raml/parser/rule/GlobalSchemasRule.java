package org.raml.parser.rule;

import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class GlobalSchemasRule extends SequenceTupleRule
{

    private Map<String, String> schemas = new HashMap<String, String>();
    private Map<String, Tag> tags = new HashMap<String, Tag>();

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
            ScalarNode valueNode = (ScalarNode) nodeTuple.getValueNode();
            schemas.put(schemaKey, valueNode.getValue());
            tags.put(schemaKey, valueNode.getTag());
            return super.getRuleForTuple(nodeTuple);
        }
    }

    public Map<String, String> getSchemas()
    {
        return schemas;
    }

    public Map<String, Tag> getTags()
    {
        return tags;
    }
}
