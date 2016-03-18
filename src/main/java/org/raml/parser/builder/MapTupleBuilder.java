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
package org.raml.parser.builder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.utils.ReflectionUtils;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class MapTupleBuilder extends DefaultTupleBuilder<ScalarNode, Node>
{

    private Class valueClass;
    private String fieldName;
    private TupleHandler innerTupleHandler;

    public MapTupleBuilder(Class<?> valueClass)
    {
        this(null, valueClass);
    }

    public MapTupleBuilder(String fieldName, Class<?> valueClass)
    {
        super(new DefaultScalarTupleHandler(fieldName));
        this.fieldName = fieldName;
        this.valueClass = valueClass;
    }

    protected void addBuilders()
    {
        TupleBuilder tupleBuilder;
        if (ReflectionUtils.isPojo(getValueClass()))
        {
            tupleBuilder = new PojoTupleBuilder(getValueClass());
        }
        else
        {
            tupleBuilder = new ScalarTupleBuilder(null, getValueClass());
        }
        if (innerTupleHandler != null)
        {
            tupleBuilder.setHandler(innerTupleHandler);
        }
        Map<String, TupleBuilder<?,?>> builderMap = new HashMap<String, TupleBuilder<?, ?>>();
        builderMap.put(fieldName, tupleBuilder);
        this.setChildrenTupleBuilders(builderMap);
    }

    @Override
    protected Map<String, TupleBuilder<?, ?>> getBuilders()
    {
        if (super.getBuilders().isEmpty())
        {
            addBuilders();
        }
        return super.getBuilders();
    }

    @Override
    public Object buildValue(Object parent, Node node)
    {
        final HashMap<String, Object> map = new LinkedHashMap<String, Object>();
        ReflectionUtils.setProperty(parent, getFieldName(), map);
        return map;
    }


    public Class getValueClass()
    {
        return valueClass;
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
    public String toString()
    {
        return fieldName;
    }
}
