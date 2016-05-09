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
package org.raml.v2.api.model.v08.bodies;

import java.util.List;
import org.raml.v2.api.model.v08.common.RAMLLanguageElement;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v08.system.types.StatusCodeString;


public interface Response extends RAMLLanguageElement
{

    /**
     * Responses MUST be a map of one or more HTTP status codes, where each status code itself is a map that describes that status code.
     **/
    StatusCodeString code();


    /**
     * An API's methods may support custom header values in responses. The custom, non-standard HTTP headers MUST be specified by the headers property. API's may include the the placeholder token {?} in a header name to indicate that any number of headers that conform to the specified format can be sent in responses. This is particularly useful for APIs that allow HTTP headers that conform to some naming convention to send arbitrary, custom data.
     **/
    List<Parameter> headers();


    /**
     * Each response MAY contain a body property, which conforms to the same structure as request body properties (see Body). Responses that can return more than one response code MAY therefore have multiple bodies defined. For APIs without a priori knowledge of the response types for their responses, `*&#47;*` MAY be used to indicate that responses that do not matching other defined data types MUST be accepted. Processing applications MUST match the most descriptive media type first if `*&#47;*` is used.
     **/
    List<BodyLike> body();

}