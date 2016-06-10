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
package org.raml.v2.api.model.v10.parameters;

import java.util.List;
import org.raml.v2.api.model.v10.datamodel.TypeDeclaration;
import org.raml.v2.api.model.v10.system.types.ContentType;


public interface FileTypeDeclaration extends TypeDeclaration
{

    /**
     * It should also include a new property: fileTypes, which should be a list of valid content-type strings for the file. The file type *&#47;* should be a valid value.
     **/
    List<ContentType> fileTypes();


    /**
     * The minLength attribute specifies the parameter value's minimum number of bytes.
     **/
    Long minLength();


    /**
     * The maxLength attribute specifies the parameter value's maximum number of bytes.
     **/
    Long maxLength();

}