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
package org.raml.v2.api.model.v10.resources;

import java.util.List;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.methods.Method;
import org.raml.v2.api.model.v10.methods.TraitRef;
import org.raml.v2.api.model.v10.security.SecuritySchemeRef;
import org.raml.v2.api.model.v10.system.types.MarkdownString;


public interface ResourceBase extends Annotable
{

    /**
     * Methods that are part of this resource type definition
     **/
    List<Method> methods();


    /**
     * A list of the traits to apply to all methods declared (implicitly or explicitly) for this resource. Individual methods may override this declaration
     **/
    List<TraitRef> is();


    /**
     * The resource type which this resource inherits.
     **/
    ResourceTypeRef type();


    // --def-system-mod--
    MarkdownString description();


    /**
     * The security schemes that apply to all methods declared (implicitly or explicitly) for this resource.
     **/
    List<SecuritySchemeRef> securedBy();


    /**
     * Detailed information about any URI parameters of this resource
     **/
    List<TypeDeclaration> uriParameters();

}