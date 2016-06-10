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
package org.raml.v2.api.model.v08.api;

import java.util.List;
import org.raml.v2.api.model.v08.bodies.MimeType;
import org.raml.v2.api.model.v08.common.RAMLLanguageElement;
import org.raml.v2.api.model.v08.methods.Trait;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v08.resources.Resource;
import org.raml.v2.api.model.v08.resources.ResourceType;
import org.raml.v2.api.model.v08.security.SecurityScheme;
import org.raml.v2.api.model.v08.security.SecuritySchemeRef;
import org.raml.v2.api.model.v08.system.types.FullUriTemplateString;


public interface Api extends RAMLLanguageElement
{

    /**
     * The title property is a short plain text description of the RESTful API. The value SHOULD be suitable for use as a title for the contained user documentation.
     **/
    String title();


    /**
     * If the RAML API definition is targeted to a specific API version, the API definition MUST contain a version property. The version property is OPTIONAL and should not be used if: The API itself is not versioned. The API definition does not change between versions. The API architect can decide whether a change to user documentation elements, but no change to the API's resources, constitutes a version change. The API architect MAY use any versioning scheme so long as version numbers retain the same format. For example, 'v3', 'v3.0', and 'V3' are all allowed, but are not considered to be equal.
     **/
    String version();


    /**
     * (Optional during development; Required after implementation) A RESTful API's resources are defined relative to the API's base URI. The use of the baseUri field is OPTIONAL to allow describing APIs that have not yet been implemented. After the API is implemented (even a mock implementation) and can be accessed at a service endpoint, the API definition MUST contain a baseUri property. The baseUri property's value MUST conform to the URI specification RFC2396 or a Level 1 Template URI as defined in RFC6570. The baseUri property SHOULD only be used as a reference value.
     **/
    FullUriTemplateString baseUri();


    /**
     * Base uri parameters are named parameters which described template parameters in the base uri
     **/
    List<Parameter> baseUriParameters();


    /**
     * URI parameters can be further defined by using the uriParameters property. The use of uriParameters is OPTIONAL. The uriParameters property MUST be a map in which each key MUST be the name of the URI parameter as defined in the baseUri property. The uriParameters CANNOT contain a key named version because it is a reserved URI parameter name. The value of the uriParameters property is itself a map that specifies  the property's attributes as named parameters
     **/
    List<Parameter> uriParameters();


    /**
     * A RESTful API can be reached HTTP, HTTPS, or both. The protocols property MAY be used to specify the protocols that an API supports. If the protocols property is not specified, the protocol specified at the baseUri property is used. The protocols property MUST be an array of strings, of values `HTTP` and&#47;or `HTTPS`.
     **/
    List<String> protocols();


    /**
     * (Optional) The media types returned by API responses, and expected from API requests that accept a body, MAY be defaulted by specifying the mediaType property. This property is specified at the root level of the API definition. The property's value MAY be a single string with a valid media type described in the specification.
     **/
    MimeType mediaType();


    /**
     * To better achieve consistency and simplicity, the API definition SHOULD include an OPTIONAL schemas property in the root section. The schemas property specifies collections of schemas that could be used anywhere in the API definition. The value of the schemas property is an array of maps; in each map, the keys are the schema name, and the values are schema definitions. The schema definitions MAY be included inline or by using the RAML !include user-defined data type.
     **/
    List<GlobalSchema> schemas();


    /**
     * Declarations of traits used in this API
     **/
    List<Trait> traits();


    /**
     * A list of the security schemes to apply to all methods, these must be defined in the securitySchemes declaration.
     **/
    List<SecuritySchemeRef> securedBy();


    /**
     * Security schemes that can be applied using securedBy
     **/
    List<SecurityScheme> securitySchemes();


    /**
     * Declaration of resource types used in this API
     **/
    List<ResourceType> resourceTypes();


    /**
     * Resources are identified by their relative URI, which MUST begin with a slash (&#47;). A resource defined as a root-level property is called a top-level resource. Its property's key is the resource's URI relative to the baseUri. A resource defined as a child property of another resource is called a nested resource, and its property's key is its URI relative to its parent resource's URI. Every property whose key begins with a slash (&#47;), and is either at the root of the API definition or is the child property of a resource property, is a resource property. The key of a resource, i.e. its relative URI, MAY consist of multiple URI path fragments separated by slashes; e.g. `&#47;bom&#47;items` may indicate the collection of items in a bill of materials as a single resource. However, if the individual URI path fragments are themselves resources, the API definition SHOULD use nested resources to describe this structure; e.g. if `&#47;bom` is itself a resource then `&#47;items` should be a nested resource of `&#47;bom`, while `&#47;bom&#47;items` should not be used.
     **/
    List<Resource> resources();


    /**
     * The API definition can include a variety of documents that serve as a user guides and reference documentation for the API. Such documents can clarify how the API works or provide business context. Documentation-generators MUST include all the sections in an API definition's documentation property in the documentation output, and they MUST preserve the order in which the documentation is declared. To add user documentation to the API, include the documentation property at the root of the API definition. The documentation property MUST be an array of documents. Each document MUST contain title and content attributes, both of which are REQUIRED. If the documentation property is specified, it MUST include at least one document. Documentation-generators MUST process the content field as if it was defined using Markdown.
     **/
    List<DocumentationItem> documentation();


    /**
     * Returns AST node of security scheme, this reference refers to, or null.
     **/
    String ramlVersion();

}