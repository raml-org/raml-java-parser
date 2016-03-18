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

import org.raml.parser.utils.ReflectionUtils;
import org.raml.parser.visitor.SchemaCompiler;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class GlobalSchemaScalarTupleBuilder extends ScalarTupleBuilder
{

    private String key;

    public GlobalSchemaScalarTupleBuilder()
    {
        super(null, String.class);
    }

    @Override
    public Object buildValue(Object parent, ScalarNode node)
    {
        String value = SchemaCompiler.encodeIncludePath(node);
        ReflectionUtils.setProperty(parent, key, value);
        return parent;
    }

    @Override
    public void buildKey(Object parent, ScalarNode tuple)
    {
        super.buildKey(parent, tuple);
        this.key = tuple.getValue();
    }
}
