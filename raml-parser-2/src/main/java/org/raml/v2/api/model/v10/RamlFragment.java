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
package org.raml.v2.api.model.v10;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

public enum RamlFragment
{
    DocumentationItem,
    DataType,
    NamedExample,
    ResourceType,
    Trait,
    AnnotationTypeDeclaration,
    Library,
    Overlay,
    Extension,
    SecurityScheme,
    Default;


    @Nullable
    public static RamlFragment byName(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return RamlFragment.Default;
        }
        else
        {
            try
            {
                return RamlFragment.valueOf(name);
            }
            catch (IllegalArgumentException iae)
            {
                return null;
            }
        }
    }

}
