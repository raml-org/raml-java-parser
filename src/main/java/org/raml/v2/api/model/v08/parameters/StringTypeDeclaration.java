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
package org.raml.v2.api.model.v08.parameters;

import java.util.List;


public interface StringTypeDeclaration extends Parameter
{

    /**
     * (Optional, applicable only for parameters of type string) The pattern attribute is a regular expression that a parameter of type string MUST match. Regular expressions MUST follow the regular expression specification from ECMA 262&#47;Perl 5. The pattern MAY be enclosed in double quotes for readability and clarity.
     **/
    String pattern();


    /**
     * (Optional, applicable only for parameters of type string) The enum attribute provides an enumeration of the parameter's valid values. This MUST be an array. If the enum attribute is defined, API clients and servers MUST verify that a parameter's value matches a value in the enum array. If there is no matching value, the clients and servers MUST treat this as an error.
     **/
    List<String> enumValues();


    /**
     * (Optional, applicable only for parameters of type string) The minLength attribute specifies the parameter value's minimum number of characters.
     **/
    Integer minLength();


    /**
     * (Optional, applicable only for parameters of type string) The maxLength attribute specifies the parameter value's maximum number of characters.
     **/
    Integer maxLength();

}