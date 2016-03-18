/*
 * Copyright 2016 (c) MuleSoft, Inc.
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

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.raml.model.parameter.Header;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.annotation.Mapping;

public class SecuritySchemeDescriptor implements Serializable
{

    private static final long serialVersionUID = 8752760480882952807L;

    @Mapping
    private Map<String, Header> headers = new LinkedHashMap<String, Header>();

    @Mapping
    private Map<String, QueryParameter> queryParameters = new LinkedHashMap<String, QueryParameter>();

    @Mapping
    private Map<String, Response> responses = new LinkedHashMap<String, Response>();

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

    public Map<String, Response> getResponses()
    {
        return responses;
    }

    public void setResponses(Map<String, Response> responses)
    {
        this.responses = responses;
    }
}
