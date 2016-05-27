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

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.type.TypeFacets;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.v2.internal.utils.NodeSelector.selectBooleanValue;
import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;
import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class ObjectTypeFacets extends BaseTypeFacets
{
    private Integer minProperties;
    private Integer maxProperties;
    private Boolean additionalProperties;
    private String discriminator;
    private String discriminatorValue;

    private Map<String, PropertyFacets> properties = new HashMap<>();

    public ObjectTypeFacets(Integer minProperties, Integer maxProperties, Boolean additionalProperties, String discriminator, String discriminatorValue,
            Map<String, PropertyFacets> properties)
    {
        this.minProperties = minProperties;
        this.maxProperties = maxProperties;
        this.additionalProperties = additionalProperties;
        this.discriminator = discriminator;
        this.discriminatorValue = discriminatorValue;
        this.properties = properties;
    }

    public ObjectTypeFacets()
    {
    }

    protected ObjectTypeFacets copy()
    {
        return new ObjectTypeFacets(minProperties, maxProperties, additionalProperties, discriminator, discriminatorValue, new HashMap<>(properties));
    }

    @Override
    public TypeFacets overwriteFacets(TypeDeclarationNode from)
    {
        final ObjectTypeFacets result = copy();
        result.setMinProperties(selectIntValue("minProperties", from));
        result.setMaxProperties(selectIntValue("maxProperties", from));
        result.setAdditionalProperties(selectBooleanValue("additionalProperties", from));
        result.setDiscriminator(selectStringValue("discriminator", from));
        result.setDiscriminatorValue(selectStringValue("discriminatorValue", from));
        final Node properties = from.get("properties");
        if (properties != null)
        {
            final List<Node> children = properties.getChildren();
            for (Node child : children)
            {
                if (child instanceof PropertyNode)
                {
                    final PropertyNode propertyNode = (PropertyNode) child;
                    final String name = propertyNode.getName();
                    final PropertyFacets propertyDefinition = new PropertyFacets(propertyNode);
                    result.getProperties().put(name, propertyDefinition);
                }
            }
        }
        return overwriteFacets(result, from);
    }

    @Override
    public TypeFacets mergeFacets(TypeFacets with)
    {
        final ObjectTypeFacets result = copy();
        if (with instanceof ObjectTypeFacets)
        {
            result.setMinProperties(((ObjectTypeFacets) with).getMinProperties());
            result.setMaxProperties(((ObjectTypeFacets) with).getMaxProperties());
            result.setAdditionalProperties(((ObjectTypeFacets) with).getAdditionalProperties());
            result.setDiscriminator(((ObjectTypeFacets) with).getDiscriminator());
            result.setDiscriminatorValue(((ObjectTypeFacets) with).getDiscriminatorValue());
            final Map<String, PropertyFacets> properties = ((ObjectTypeFacets) with).getProperties();
            for (Map.Entry<String, PropertyFacets> property : properties.entrySet())
            {
                if (!getProperties().containsKey(property.getKey()))
                {
                    result.getProperties().put(property.getKey(), property.getValue());
                }
                else
                {
                    // If present in both merge facets of both types
                    final PropertyFacets propertyDefinition = result.getProperties().get(property.getKey());
                    result.getProperties().put(property.getKey(), propertyDefinition.mergeFacets(property.getValue()));
                }
            }
        }
        return mergeFacets(result, with);

    }

    @Override
    public <T> T visit(TypeFacetsVisitor<T> visitor)
    {
        return visitor.visitObject(this);
    }

    public Integer getMinProperties()
    {
        return minProperties;
    }

    public void setMinProperties(Integer minProperties)
    {
        if (minProperties != null)
        {
            this.minProperties = minProperties;
        }
    }

    public Integer getMaxProperties()
    {
        return maxProperties;
    }

    public void setMaxProperties(Integer maxProperties)
    {
        if (maxProperties != null)
        {
            this.maxProperties = maxProperties;
        }
    }

    public Boolean getAdditionalProperties()
    {
        return additionalProperties;
    }

    public void setAdditionalProperties(Boolean additionalProperties)
    {
        if (additionalProperties != null)
        {
            this.additionalProperties = additionalProperties;
        }
    }

    public String getDiscriminator()
    {
        return discriminator;
    }

    public void setDiscriminator(String discriminator)
    {
        if (discriminator != null)
        {
            this.discriminator = discriminator;
        }
    }

    public String getDiscriminatorValue()
    {
        return discriminatorValue;
    }

    public void setDiscriminatorValue(String discriminatorValue)
    {
        if (discriminatorValue != null)
        {
            this.discriminatorValue = discriminatorValue;
        }
    }

    public Map<String, PropertyFacets> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, PropertyFacets> properties)
    {
        if (properties != null && !properties.isEmpty())
        {
            this.properties = properties;
        }
    }
}
