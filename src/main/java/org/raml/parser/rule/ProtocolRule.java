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

import org.raml.model.Protocol;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ConvertUtils;
import org.yaml.snakeyaml.nodes.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.parser.rule.ValidationMessage.getRuleTypeMisMatch;

/**
 * Created. There, you have it.
 */
public class ProtocolRule extends DefaultTupleRule<Node, Node> implements SequenceRule {

    private static final DefaultScalarTupleHandler PROTOCOL_HANDLER = new DefaultScalarTupleHandler("protocols");
    protected Map<String, TupleRule<?, ?>> rules = new HashMap<String, TupleRule<?, ?>>();

    @Override
    public TupleHandler getHandler() {
        return PROTOCOL_HANDLER;
    }

    protected boolean isValidValueNodeType(Class valueNodeClass)
    {
        for (Class<?> clazz : getValueNodeType())
        {
            if (clazz.isAssignableFrom(valueNodeClass))
            {
                return true;
            }
        }
        return false;
    }

    public Class<?>[] getValueNodeType()
    {
        return new Class[] {ScalarNode.class, SequenceNode.class};
    }

    @Override
    public NodeRule<?> getItemRule() {
        return this;
    }

    @Override
    public List<ValidationResult> doValidateValue(Node value) {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();

        if ( value instanceof ScalarNode ) {

            if (!ConvertUtils.canBeConverted(((ScalarNode)value).getValue(), Protocol.class))
            {
                validationResults.add(ValidationResult.createErrorResult(getRuleTypeMisMatch(getName(), Protocol.class.getSimpleName()), value));
            }
        }

        return validationResults;
    }
}
