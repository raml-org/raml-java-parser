/*
 * Copyright 2016 (c) MuleSoft, Inc.
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

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleRule extends DefaultTupleRule<ScalarNode, MappingNode> implements TypedTupleRule
{

    private Class valueType;
    private String fieldName;
    private final Set<String> keys = new HashSet<String>();
    private TupleHandler innerTupleHandler;

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
            tupleRule = getScalarRule();
        }
        if (innerTupleHandler != null && !innerTupleHandler.handles(nodeTuple))
        {
            return new UnknownTupleRule<Node, Node>(nodeTuple.getKeyNode().toString());
        }
        tupleRule.setParentTupleRule(this);
        return tupleRule;
    }

    protected DefaultTupleRule getScalarRule()
    {
        return new SimpleRule(getFieldName(), getValueType());
    }

    public Class getValueType()
    {
        return valueType;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setInnerTupleHandler(TupleHandler innerTupleHandler)
    {
        this.innerTupleHandler = innerTupleHandler;
    }

    @Override
    public void setValueType(Type valueType)
    {
        this.valueType = (Class) valueType;
    }

    @Override
    public TupleRule<?, ?> deepCopy()
    {
        checkClassToCopy(MapTupleRule.class);
        MapTupleRule copy = new MapTupleRule(getName(), valueType);
        copy.setHandler(getHandler());
        copy.setInnerTupleHandler(innerTupleHandler);
        copy.setNodeRuleFactory(getNodeRuleFactory());
        return copy;
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
