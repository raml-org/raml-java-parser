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
package org.raml.v2.api.model.v08.resources;

import java.util.List;
import org.raml.v2.api.model.v08.common.RAMLLanguageElement;
import org.raml.v2.api.model.v08.methods.Method;
import org.raml.v2.api.model.v08.methods.TraitRef;
import org.raml.v2.api.model.v08.parameters.Parameter;
import org.raml.v2.api.model.v08.security.SecuritySchemeRef;
import org.raml.v2.api.model.v08.system.types.TypeInstance;


public interface ResourceType extends RAMLLanguageElement
{

    /**
     * Name of the resource type
     **/
    String name();


    /**
     * Instructions on how and when the resource type should be used.
     **/
    String usage();


    /**
     * Methods that are part of this resource type definition
     **/
    List<Method> methods();


    /**
     * Instantiation of applyed traits
     **/
    List<TraitRef> is();


    /**
     * Instantiation of applyed resource type
     **/
    ResourceTypeRef type();


    /**
     * securityScheme may also be applied to a resource by using the securedBy key, which is equivalent to applying the securityScheme to all methods that may be declared, explicitly or implicitly, by defining the resourceTypes or traits property for that resource. To indicate that the method may be called without applying any securityScheme, the method may be annotated with the null securityScheme.
     **/
    List<SecuritySchemeRef> securedBy();


    /**
     * Uri parameters of this resource
     **/
    List<Parameter> uriParameters();


    /**
     * An alternate, human-friendly name for the resource type
     **/
    String displayName();


    /**
     * A resource or a method can override a base URI template's values. This is useful to restrict or change the default or parameter selection in the base URI. The baseUriParameters property MAY be used to override any or all parameters defined at the root level baseUriParameters property, as well as base URI parameters not specified at the root level.
     **/
    List<Parameter> baseUriParameters();


    /**
     * Returns object representation of parametrized properties of the resource type
     **/
    TypeInstance parametrizedProperties();

}