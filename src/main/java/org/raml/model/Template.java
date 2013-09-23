package org.raml.model;

import org.raml.parser.annotation.Scalar;

public class Template
{

    @Scalar
    private String description;

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
}
