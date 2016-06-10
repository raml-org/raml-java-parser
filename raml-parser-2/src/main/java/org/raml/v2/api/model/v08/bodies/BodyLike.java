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
import org.raml.v2.api.model.v08.system.types.ExampleString;
import org.raml.v2.api.model.v08.system.types.SchemaString;


public interface BodyLike extends RAMLLanguageElement
{

    /**
     * Mime type of the request or response body
     **/
    String name();


    /**
     * The structure of a request or response body MAY be further specified by the schema property under the appropriate media type. The schema key CANNOT be specified if a body's media type is application&#47;x-www-form-urlencoded or multipart&#47;form-data. All parsers of RAML MUST be able to interpret JSON Schema and XML Schema. Schema MAY be declared inline or in an external file. However, if the schema is sufficiently large so as to make it difficult for a person to read the API definition, or the schema is reused across multiple APIs or across multiple miles in the same API, the !include user-defined data type SHOULD be used instead of including the content inline. Alternatively, the value of the schema field MAY be the name of a schema specified in the root-level schemas property, or it MAY be declared in an external file and included by using the by using the RAML !include user-defined data type.
     **/
    SchemaString schema();


    /**
     * Documentation generators MUST use body properties' example attributes to generate example invocations.
     **/
    ExampleString example();


    /**
     * Web forms REQUIRE special encoding and custom declaration. If the API's media type is either application&#47;x-www-form-urlencoded or multipart&#47;form-data, the formParameters property MUST specify the name-value pairs that the API is expecting. The formParameters property is a map in which the key is the name of the web form parameter, and the value is itself a map the specifies the web form parameter's attributes.
     **/
    List<Parameter> formParameters();


    /**
     * Returns schema content for the cases when schema is inlined, when schema is included, and when schema is a reference.
     **/
    String schemaContent();

}