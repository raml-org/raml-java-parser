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
package org.raml.v2.api.model.v10.datamodel;

import java.util.List;


public interface ObjectTypeDeclaration extends TypeDeclaration
{

    /**
     * The properties that instances of this type may or must have.
     **/
    List<TypeDeclaration> properties();


    /**
     * The minimum number of properties allowed for instances of this type.
     **/
    Integer minProperties();


    /**
     * The maximum number of properties allowed for instances of this type.
     **/
    Integer maxProperties();

    Boolean additionalProperties();

    /**
     * Type property name to be used as discriminator, or boolean
     **/
    String discriminator();


    /**
     * The value of discriminator for the type.
     **/
    String discriminatorValue();

}