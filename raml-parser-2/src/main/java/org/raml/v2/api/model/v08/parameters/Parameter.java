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

import org.raml.v2.api.model.v08.common.RAMLLanguageElement;


public interface Parameter extends RAMLLanguageElement
{

    /**
     * name of the parameter
     **/
    String name();


    /**
     * An alternate, human-friendly name for the parameter
     **/
    String displayName();


    /**
     * The type attribute specifies the primitive type of the parameter's resolved value. API clients MUST return&#47;throw an error if the parameter's resolved value does not match the specified type. If type is not specified, it defaults to string.
     **/
    String type();


    /**
     * Set to true if parameter is required
     **/
    Boolean required();


    /**
     * The default attribute specifies the default value to use for the property if the property is omitted or its value is not specified. This SHOULD NOT be interpreted as a requirement for the client to send the default attribute's value if there is no other value to send. Instead, the default attribute's value is the value the server uses if the client does not send a value.
     **/
    String defaultValue();


    /**
     * (Optional) The example attribute shows an example value for the property. This can be used, e.g., by documentation generators to generate sample values for the property.
     **/
    String example();


    /**
     * The repeat attribute specifies that the parameter can be repeated. If the parameter can be used multiple times, the repeat parameter value MUST be set to 'true'. Otherwise, the default value is 'false' and the parameter may not be repeated.
     **/
    Boolean repeat();

}