package org.raml.model;

import java.util.HashMap;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

public class TraitAction
{

    @Key
    private String type;

    @Scalar
    private String summary;

    @Scalar
    private String description;

    @Mapping
    private Map<String, Header> headers = new HashMap<String, Header>();

    @Mapping
    private Map<String, QueryParameter> queryParameters = new HashMap<String, QueryParameter>();

    @Mapping
    private Map<String, MimeType> body = new HashMap<String, MimeType>();

    @Mapping
    private Map<String, Response> responses = new HashMap<String, Response>();


    public TraitAction()
    {
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Map<String, Header> getHeaders()
    {
        return headers;
    }

    public void setHeaders(Map<String, Header> headers)
    {
        this.headers = headers;
    }

    public Map<String, QueryParameter> getQueryParameters()
    {
        return queryParameters;
    }

    public void setQueryParameters(Map<String, QueryParameter> queryParameters)
    {
        this.queryParameters = queryParameters;
    }

    public Map<String, MimeType> getBody()
    {
        return body;
    }

    public void setBody(Map<String, MimeType> body)
    {
        this.body = body;
    }

    public Map<String, Response> getResponses()
    {
        return responses;
    }

    public void setResponses(Map<String, Response> responses)
    {
        this.responses = responses;
    }

    @Override
    public String toString()
    {
        return "Action{" +
               "type='" + type + '}';
    }

    public Action createAction(Resource resource)
    {
        Action action = new Action();
        action.setType(ActionType.valueOf(type.toUpperCase()));
        action.setDescription(description);
        action.setHeaders(headers);
        action.setQueryParameters(queryParameters);
        action.setBody(body);
        action.setResponses(responses);
        action.setResource(resource);
        return action;
    }

    public void updateAction(Action action)
    {
        if (action.getDescription() == null)
        {
            action.setDescription(description);
        }
        for (String key : headers.keySet())
        {
            if (action.getHeaders().get(key) == null)
            {
                action.getHeaders().put(key, headers.get(key));
            }
        }
        for (String key : queryParameters.keySet())
        {
            if (action.getQueryParameters().get(key) == null)
            {
                action.getQueryParameters().put(key, queryParameters.get(key));
            }
        }
        for (String key : body.keySet())
        {
            if (action.getBody().get(key) == null)
            {
                action.getBody().put(key, body.get(key));
            }
        }
        for (String key : responses.keySet())
        {
            if (action.getResponses().get(key) == null)
            {
                action.getResponses().put(key, responses.get(key));
            }
        }
    }
}
