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


public interface NumberTypeDeclaration extends Parameter
{

    /**
     * (Optional, applicable only for parameters of type number or integer) The minimum attribute specifies the parameter's minimum value.
     **/
    Double minimum();


    /**
     * (Optional, applicable only for parameters of type number or integer) The maximum attribute specifies the parameter's maximum value.
     **/
    Double maximum();

}