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

import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nullable;

public class PropertyFacets
{

    private String name;
    private ResolvedType resolvedType;
    private boolean required;
    private PropertyNode propertyNode;
    private ErrorNode errorNode;

    public PropertyFacets(String name, ResolvedType resolvedType, Boolean required)
    {
        this.name = name;
        this.resolvedType = resolvedType;
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

    public ResolvedType getValueType()
    {
        // Load it lazy so it support recursive definitions
        if (resolvedType == null)
        {
            resolvedType = propertyNode.getTypeDefinition();
        }
        return resolvedType;
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
        checkOptionalPropertyOverRequired(value);
        return new PropertyFacets(name, getValueType().mergeFacets(value.getValueType()), required || value.isRequired());
    }

    public PropertyFacets overwriteFacets(PropertyFacets value)
    {
        checkOptionalPropertyOverRequired(value);
        return new PropertyFacets(name, value.getValueType(), required || value.isRequired());
    }

    private void checkOptionalPropertyOverRequired(PropertyFacets value)
    {
        if (required && !value.isRequired())
        {
            value.errorNode = RamlErrorNodeFactory.createInvalidRequiredFacet(name);
        }
    }

    public Node getErrorNode()
    {
        return errorNode;
    }
}
