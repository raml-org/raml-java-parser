package org.raml.model;

import java.util.HashMap;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Value;
import org.raml.parser.resolver.MatchAllHandler;

public class TemplateReference
{


    private String name;

    @Mapping(handler = MatchAllHandler.class)
    private Map<String, String> parameters = new HashMap<String, String>();


    public TemplateReference(@Value String name)
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
