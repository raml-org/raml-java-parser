/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
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
            //TODO check key node type
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
