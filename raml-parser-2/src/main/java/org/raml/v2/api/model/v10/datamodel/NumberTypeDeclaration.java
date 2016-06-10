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


public interface NumberTypeDeclaration extends TypeDeclaration
{

    /**
     * (Optional, applicable only for parameters of type number or integer) The minimum attribute specifies the parameter's minimum value.
     **/
    Double minimum();


    /**
     * (Optional, applicable only for parameters of type number or integer) The maximum attribute specifies the parameter's maximum value.
     **/
    Double maximum();


    /**
     * (Optional, applicable only for parameters of type string) The enum attribute provides an enumeration of the parameter's valid values. This MUST be an array. If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. If there is no matching value, the clients and servers MUST treat this as an error.
     **/
    List<Number> enumValues();


    /**
     * Value format
     **/
    String format();


    /**
     * A numeric instance is valid against "multipleOf" if the result of the division of the instance by this keyword's value is an integer.
     **/
    Double multipleOf();

}