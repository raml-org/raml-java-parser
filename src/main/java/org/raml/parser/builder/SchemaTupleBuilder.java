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

import org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import org.raml.parser.utils.NodeUtils;
import org.raml.parser.utils.ReflectionUtils;
import org.raml.parser.visitor.SchemaCompiler;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SchemaTupleBuilder extends ScalarTupleBuilder
{

    private static final String SCHEMA_FIELD_NAME = "schema";
    private static final String PARSED_SCHEMA_FIELD_NAME = "compiledSchema";

    public SchemaTupleBuilder()
    {
        super(SCHEMA_FIELD_NAME, String.class);
    }

    @Override
    public Object buildValue(Object parent, ScalarNode node)
    {
        Object result = super.buildValue(parent, node);
        Object schema = compileSchema(node);
        ReflectionUtils.setProperty(parent, PARSED_SCHEMA_FIELD_NAME, schema);
        return result;
    }

    private Object compileSchema(ScalarNode node)
    {
        String value = node.getValue();

        if (value == null || NodeUtils.isNonStringTag(node.getTag()))
        {
            return null;
        }

        Object schema = null;
        String mimeType = getParent() instanceof PojoTupleBuilder ? ((PojoTupleBuilder) getParent()).getFieldName() : null;
        if (mimeType != null && mimeType.contains("xml"))
        {
            schema = SchemaCompiler.getInstance().compile(value);
        }
        else if (mimeType != null && mimeType.contains("json") && (node instanceof IncludeScalarNode))
        {
            //in case of json schemas store include path
            schema = ((IncludeScalarNode) node).getIncludeName();
        }
        return schema;
    }

}
