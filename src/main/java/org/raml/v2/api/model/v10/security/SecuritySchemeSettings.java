/*
 * Copyright 2013 (c) MuleSoft, Inc.
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
package org.raml.v2.api.model.v10.security;

import java.util.List;

import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.system.types.FixedUriString;


public interface SecuritySchemeSettings extends Annotable
{

    /**
     * The URI of the Temporary Credential Request endpoint as defined in RFC5849 Section 2.1
     * (OAuth 1)
     **/
    FixedUriString requestTokenUri();


    /**
     * The URI of the Resource Owner Authorization endpoint as defined in RFC5849 Section 2.2
     * (OAuth 1)
     *
     * The URI of the Authorization Endpoint as defined in RFC6749 Section 3.1. Required forby authorization_code and implicit grant types.
     * (OAuth 2)
     **/
    FixedUriString authorizationUri();


    /**
     * The URI of the Token Request endpoint as defined in RFC5849 Section 2.3
     * (OAuth 1)
     **/
    FixedUriString tokenCredentialsUri();


    /**
     * List of the signature methods used by the server. Available methods: HMAC-SHA1, RSA-SHA1, PLAINTEXT
     * (OAuth 1)
     **/
    List<String> signatures();


    /**
     * The URI of the Token Endpoint as defined in RFC6749 Section 3.2. Not required forby implicit grant type.
     * (OAuth 2)
     **/
    FixedUriString accessTokenUri();


    /**
     * A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of: authorization_code, password, client_credentials, implicit, or refresh_token.
     * (OAuth 2)
     **/
    List<String> authorizationGrants();


    /**
     * A list of scopes supported by the security scheme as defined in RFC6749 Section 3.3
     * (OAuth 2)
     **/
    List<String> scopes();

}