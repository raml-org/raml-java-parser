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

public class ObjectPropertyDefinition
{

    private String name;
    private TypeFacets typeFacets;
    private Boolean required;
    private PropertyNode propertyNode;

    public ObjectPropertyDefinition(String name, TypeFacets typeFacets, Boolean required)
    {
        this.name = name;
        this.typeFacets = typeFacets;
        this.required = required;
    }

    public ObjectPropertyDefinition(PropertyNode propertyNode)
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

    public Boolean getRequired()
    {
        return required;
    }

    public ObjectPropertyDefinition mergeFacets(ObjectPropertyDefinition value)
    {
        return new ObjectPropertyDefinition(name, getTypeFacets().mergeFacets(value.getTypeFacets()), required || value.getRequired());
    }
}
