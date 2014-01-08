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
package org.raml.parser.builder;

import java.lang.reflect.Field;

import org.raml.parser.annotation.Scalar;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import org.raml.parser.utils.ConvertUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class ScalarTupleBuilder extends DefaultTupleBuilder<ScalarNode, ScalarNode>
{

    private String fieldName;
    private Class<?> type;
    private String includeField;

    public ScalarTupleBuilder(String field, Class<?> type,String includeField)
    {
        super(new DefaultScalarTupleHandler(field));
        this.type = type;
        this.includeField=includeField;
    }


    @Override
    public Object buildValue(Object parent, ScalarNode node)
    {

        final String value = node.getValue();
        final Object converted = ConvertUtils.convertTo(value, type);
        String unalias = unalias(parent, fieldName);
        ReflectionUtils.setProperty(parent, unalias, converted);
        if (includeField!=null&&includeField.length()>0){
        	if (node instanceof IncludeScalarNode)
        	{
        		IncludeScalarNode sc=(IncludeScalarNode) node;
        		ReflectionUtils.setProperty(parent, includeField, sc.getIncludeName());
        	}
        }
        return parent;
    }

    @Override
    public void buildKey(Object parent, ScalarNode tuple)
    {
        fieldName = tuple.getValue();
    }
}
