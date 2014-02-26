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
package org.raml.model;

import java.util.List;
import java.util.Map;

import org.raml.model.parameter.FormParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

public class MimeType
{

    @Key
    private String type;

    @Scalar(rule = org.raml.parser.rule.SchemaRule.class)
    private String schema;

    @Scalar
    private String example;

    @Mapping
    private Map<String, List<FormParameter>> formParameters;

    public MimeType()
    {
    }

    public MimeType(@Key String type)
    {
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSchema()
    {
        return schema;
    }

    public void setSchema(String schema)
    {
        this.schema = schema;
    }

    public String getExample()
    {
        return example;
    }

    public void setExample(String example)
    {
        this.example = example;
    }

    public Map<String, List<FormParameter>> getFormParameters()
    {
        //TODO throw exception if invalid type?
        return formParameters;
    }

    public void setFormParameters(Map<String, List<FormParameter>> formParameters)
    {
        this.formParameters = formParameters;
    }

    @Override
    public String toString()
    {
        return "MimeType{" +
               "type='" + type + '\'' +
               '}';
    }
}
