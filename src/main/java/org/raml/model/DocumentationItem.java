package org.raml.model;

import org.raml.parser.annotation.Scalar;

public class DocumentationItem
{
    @Scalar
    private String title;
    @Scalar
    private String content;

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }
}
