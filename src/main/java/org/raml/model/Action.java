/*
 * Copyright (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;

public class Action
{

    @Key
    private ActionType type;

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

    @Parent
    private Resource resource;

    @Sequence(rule = org.raml.parser.rule.TemplateReferenceSequenceRule.class)
    private List<TemplateReference> is = new ArrayList<TemplateReference>();

    @Sequence
    private List<Protocol> protocols = new ArrayList<Protocol>();

    @Sequence
    private List<String> securedBy = new ArrayList<String>();

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, List<UriParameter>> baseUriParameters = new HashMap<String, List<UriParameter>>();

    public Action()
    {
    }

    public ActionType getType()
    {
        return type;
    }

    public void setType(ActionType type)
    {
        this.type = type;
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

    public Resource getResource()
    {
        return resource;
    }

    public void setResource(Resource resource)
    {
        this.resource = resource;
    }

    public List<TemplateReference> getIs()
    {
        return is;
    }

    public void setIs(List<TemplateReference> is)
    {
        this.is = is;
    }

    public List<Protocol> getProtocols()
    {
        return protocols;
    }

    public void setProtocols(List<Protocol> protocols)
    {
        this.protocols = protocols;
    }

    public List<String> getSecuredBy()
    {
        return securedBy;
    }

    public void setSecuredBy(List<String> securedBy)
    {
        this.securedBy = securedBy;
    }

    public Map<String, List<UriParameter>> getBaseUriParameters()
    {
        return baseUriParameters;
    }

    public void setBaseUriParameters(Map<String, List<UriParameter>> baseUriParameters)
    {
        this.baseUriParameters = baseUriParameters;
    }

    @Override
    public String toString()
    {
        return "Action{" +
               "type='" + type + '\'' +
               ", resource=" + (resource != null ? resource.getUri() : "-") + '}';
    }
}
