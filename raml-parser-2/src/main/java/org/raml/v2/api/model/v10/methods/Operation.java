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
package org.raml.v2.api.model.v10.methods;

import java.util.List;
import org.raml.v2.api.model.v10.bodies.Response;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;


public interface Operation extends Annotable
{

    /**
     * An APIs resources MAY be filtered (to return a subset of results) or altered (such as transforming  a response body from JSON to XML format) by the use of query strings. If the resource or its method supports a query string, the query string MUST be defined by the queryParameters property
     **/
    List<TypeDeclaration> queryParameters();


    /**
     * Headers that allowed at this position
     **/
    List<TypeDeclaration> headers();


    /**
     * Specifies the query string needed by this method. Mutually exclusive with queryParameters.
     **/
    TypeDeclaration queryString();


    /**
     * Information about the expected responses to a request
     **/
    List<Response> responses();

}