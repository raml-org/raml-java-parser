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


public interface ArrayTypeDeclaration extends TypeDeclaration
{

    /**
     * Should items in array be unique
     **/
    Boolean uniqueItems();


    /**
     * Array component type.
     **/
    TypeDeclaration items();


    /**
     * Minimum amount of items in array
     **/
    Integer minItems();


    /**
     * Maximum amount of items in array
     **/
    Integer maxItems();

}