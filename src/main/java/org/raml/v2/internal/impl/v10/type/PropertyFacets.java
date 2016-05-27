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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;

import javax.annotation.Nullable;

public class PropertyFacets
{

    private String name;
    private TypeFacets typeFacets;
    private boolean required;
    private PropertyNode propertyNode;

    public PropertyFacets(String name, TypeFacets typeFacets, Boolean required)
    {
        this.name = name;
        this.typeFacets = typeFacets;
        this.required = required;
    }

    public PropertyFacets(PropertyNode propertyNode)
    {
        this.name = propertyNode.getName();
        this.required = propertyNode.isRequired();
        this.propertyNode = propertyNode;
    }

    public String getName()
    {
        return name;
    }

    public TypeFacets getTypeFacets()
    {
        // Load it lazy so it support recursive definitions
        if (typeFacets == null)
        {
            typeFacets = propertyNode.getTypeDefinition();
        }
        return typeFacets;
    }

    public boolean isRequired()
    {
        return required;
    }

    public boolean isPatternProperty()
    {
        return name.startsWith("/") && name.endsWith("/");
    }

    @Nullable
    public String getPatternRegex()
    {
        if (isPatternProperty())
        {
            return name.substring(1, name.length() - 1);
        }
        else
        {
            return null;
        }
    }

    public PropertyFacets mergeFacets(PropertyFacets value)
    {
        return new PropertyFacets(name, getTypeFacets().mergeFacets(value.getTypeFacets()), required || value.isRequired());
    }
}
