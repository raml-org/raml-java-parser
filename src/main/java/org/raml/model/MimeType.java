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
