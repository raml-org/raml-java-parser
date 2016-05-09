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

import org.raml.v2.api.model.v10.common.Annotable;


public interface XMLFacetInfo extends Annotable
{

    /**
     * If attribute is set to true, a type instance should be serialized as an XML attribute. It can only be true for scalar types.
     **/
    Boolean attribute();


    /**
     * If wrapped is set to true, a type instance should be wrapped in its own XML element. It can not be true for scalar types and it can not be true at the same moment when attribute is true.
     **/
    Boolean wrapped();


    /**
     * Allows to override the name of the XML element or XML attribute in it's XML representation.
     **/
    String name();


    /**
     * Allows to configure the name of the XML namespace.
     **/
    String namespace();


    /**
     * Allows to configure the prefix which will be used during serialization to XML.
     **/
    String prefix();

}