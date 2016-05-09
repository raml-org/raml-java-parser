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
package org.raml.v2.api.model.v08.security;

import java.util.List;
import org.raml.v2.api.model.v08.methods.MethodBase;
import org.raml.v2.api.model.v08.methods.TraitRef;


public interface SecuritySchemePart extends MethodBase
{

    /**
     * An alternate, human-friendly name for the security scheme part
     **/
    String displayName();


    /**
     * Instantiation of applyed traits
     **/
    List<TraitRef> is();

}