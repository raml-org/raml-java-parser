package org.raml.model;

import java.util.HashMap;
import java.util.Map;

import org.raml.parser.annotation.Mapping;

public class Response
{

    @Mapping
    private Map<String, MimeType> body = new HashMap<String, MimeType>();

    public void setBody(Map<String, MimeType> body)
    {
        this.body = body;
    }

    public Map<String, MimeType> getBody()
    {
        return body;
    }
}
