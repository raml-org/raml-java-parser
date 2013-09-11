package org.raml.model;

import java.util.Map;

import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;

public class TemplateReference
{

    @Key
    private String name;
    @Mapping(implicit = true)
    private Map<String, String> parameters;

    public TemplateReference()
    {
    }

    public TemplateReference(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Map<String, String> getParameters()
    {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters)
    {
        this.parameters = parameters;
    }
}
