package org.raml.model;

import java.util.HashMap;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

public class Response
{

    @Scalar
    private String description;

    @Mapping
    private Map<String, MimeType> body = new HashMap<String, MimeType>();

    @Mapping
    private Map<String, Header> headers = new HashMap<String, Header>();

    public Map<String, Header> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, Header> headers)
    {
        this.headers = headers;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public void setBody(Map<String, MimeType> body)
    {
        this.body = body;
    }

    public Map<String, MimeType> getBody()
    {
        return body;
    }
}
