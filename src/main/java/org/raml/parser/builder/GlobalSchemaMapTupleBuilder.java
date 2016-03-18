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
import java.util.Map;

public class GlobalSchemaMapTupleBuilder extends MapTupleBuilder
{

    public static final Class<String> VALUE_CLASS = String.class;

    public GlobalSchemaMapTupleBuilder()
    {
        super(VALUE_CLASS);
    }

    protected void addBuilders()
    {
        TupleBuilder tupleBuilder = new GlobalSchemaScalarTupleBuilder();
        Map<String, TupleBuilder<?,?>> builderMap = new HashMap<String, TupleBuilder<?, ?>>();
        builderMap.put(null, tupleBuilder);
        this.setChildrenTupleBuilders(builderMap);
    }
}
