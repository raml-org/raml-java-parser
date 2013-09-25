package org.raml.model;

import org.raml.parser.annotation.Scalar;

public class Template
{

    @Scalar
    private String displayName;

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }
}
