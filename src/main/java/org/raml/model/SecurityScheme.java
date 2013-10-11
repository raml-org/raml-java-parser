package org.raml.model;

import java.util.HashMap;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

public class SecurityScheme
{

    @Scalar
    private String description;

    @Scalar
    private String type;

    @Scalar
    private SecuritySchemeDescriptor describedBy;

    @Mapping
    private Map<String, String> settings = new HashMap<String, String>();

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SecuritySchemeDescriptor getDescribedBy()
    {
        return describedBy;
    }

    public void setDescribedBy(SecuritySchemeDescriptor describedBy)
    {
        this.describedBy = describedBy;
    }

    public Map<String, String> getSettings()
    {
        return settings;
    }

    public void setSettings(Map<String, String> settings)
    {
        this.settings = settings;
    }
}
