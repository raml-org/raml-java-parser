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
package org.raml.v2.api.model.v08.security;

import java.util.List;
import org.raml.v2.api.model.v08.system.types.FixedUri;


public interface OAuth2SecuritySchemeSettings extends SecuritySchemeSettings
{

    /**
     * The URI of the Token Endpoint as defined in RFC6749 Section 3.2. Not required forby implicit grant type.
     **/
    FixedUri accessTokenUri();


    /**
     * The URI of the Authorization Endpoint as defined in RFC6749 Section 3.1. Required forby authorization_code and implicit grant types.
     **/
    FixedUri authorizationUri();


    /**
     * A list of the Authorization grants supported by the API as defined in RFC6749 Sections 4.1, 4.2, 4.3 and 4.4, can be any of: authorization_code, password, client_credentials, implicit, or refresh_token.
     **/
    List<String> authorizationGrants();


    /**
     * A list of scopes supported by the security scheme as defined in RFC6749 Section 3.3
     **/
    List<String> scopes();

}