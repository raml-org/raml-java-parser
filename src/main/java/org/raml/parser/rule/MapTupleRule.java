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

import static org.raml.parser.rule.ValidationMessage.getDuplicateRuleMessage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleRule extends DefaultTupleRule<ScalarNode, MappingNode>
{

    private final Class valueType;
    private String fieldName;
    private final Set<String> keys = new HashSet<String>();

    public MapTupleRule(String fieldName, Class valueType)
    {
        super(fieldName, new DefaultScalarTupleHandler(fieldName));
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
        TupleRule<?, ?> tupleRule;
        if (ReflectionUtils.isPojo(valueType))
        {
            tupleRule = new PojoTupleRule(fieldName, valueType, getNodeRuleFactory());
        }
        else
        {
            tupleRule = new SimpleRule(fieldName, valueType);
        }

        tupleRule.setParentTupleRule(this);
        return tupleRule;
    }

    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        fieldName = key.getValue();
        return super.validateKey(key);
    }

    public void checkDuplicate(ScalarNode key, List<ValidationResult> validationResults)
    {
        if (keys.contains(key.getValue()))
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage(getName()), key));
        }
        else
        {
            keys.add(key.getValue());
        }
    }
}
