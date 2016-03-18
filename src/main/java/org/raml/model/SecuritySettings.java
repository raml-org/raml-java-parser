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
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;

public class SecuritySettings implements Serializable
{

    private static final long serialVersionUID = -4243573177407087911L;

    @Scalar
    private String requestTokenUri;

    @Scalar
    private String authorizationUri;

    @Scalar
    private String tokenCredentialsUri;

    @Scalar
    private String accessTokenUri;

    @Sequence
    private List<String> authorizationGrants = new ArrayList<String>();

    @Sequence
    private List<String> scopes = new ArrayList<String>();

    public String getRequestTokenUri()
    {
        return requestTokenUri;
    }

    public void setRequestTokenUri(String requestTokenUri)
    {
        this.requestTokenUri = requestTokenUri;
    }

    public String getAuthorizationUri()
    {
        return authorizationUri;
    }

    public void setAuthorizationUri(String authorizationUri)
    {
        this.authorizationUri = authorizationUri;
    }

    public String getTokenCredentialsUri()
    {
        return tokenCredentialsUri;
    }

    public void setTokenCredentialsUri(String tokenCredentialsUri)
    {
        this.tokenCredentialsUri = tokenCredentialsUri;
    }

    public String getAccessTokenUri()
    {
        return accessTokenUri;
    }

    public void setAccessTokenUri(String accessTokenUri)
    {
        this.accessTokenUri = accessTokenUri;
    }

    public List<String> getAuthorizationGrants()
    {
        return authorizationGrants;
    }

    public void setAuthorizationGrants(List<String> authorizationGrants)
    {
        this.authorizationGrants = authorizationGrants;
    }

    public List<String> getScopes()
    {
        return scopes;
    }

    public void setScopes(List<String> scopes)
    {
        this.scopes = scopes;
    }

}
