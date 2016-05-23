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
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.raml.v2.internal.utils.NodeSelector.selectBooleanValue;
import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;
import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class ObjectTypeDefinition implements TypeDefinition
{
    private Integer minProperties;
    private Integer maxProperties;
    private Boolean additionalProperties;
    private String discriminator;
    private String discriminatorValue;

    private Map<String, ObjectPropertyDefinition> properties = new HashMap<>();

    public ObjectTypeDefinition(Integer minProperties, Integer maxProperties, Boolean additionalProperties, String discriminator, String discriminatorValue,
            Map<String, ObjectPropertyDefinition> properties)
    {
        this.minProperties = minProperties;
        this.maxProperties = maxProperties;
        this.additionalProperties = additionalProperties;
        this.discriminator = discriminator;
        this.discriminatorValue = discriminatorValue;
        this.properties = properties;
    }

    public ObjectTypeDefinition()
    {
    }

    protected ObjectTypeDefinition copy()
    {
        return new ObjectTypeDefinition(minProperties, maxProperties, additionalProperties, discriminator, discriminatorValue, new HashMap<>(properties));
    }

    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        final ObjectTypeDefinition result = copy();
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
                    final ObjectPropertyDefinition propertyDefinition = new ObjectPropertyDefinition(propertyNode);
                    result.getProperties().put(name, propertyDefinition);
                }
            }
        }
        return result;
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        final ObjectTypeDefinition result = copy();
        if (with instanceof ObjectTypeDefinition)
        {
            result.setMinProperties(((ObjectTypeDefinition) with).getMinProperties());
            result.setMaxProperties(((ObjectTypeDefinition) with).getMaxProperties());
            result.setAdditionalProperties(((ObjectTypeDefinition) with).getAdditionalProperties());
            result.setDiscriminator(((ObjectTypeDefinition) with).getDiscriminator());
            result.setDiscriminatorValue(((ObjectTypeDefinition) with).getDiscriminatorValue());
            final Map<String, ObjectPropertyDefinition> properties = ((ObjectTypeDefinition) with).getProperties();
            for (Map.Entry<String, ObjectPropertyDefinition> property : properties.entrySet())
            {
                if (!getProperties().containsKey(property.getKey()))
                {
                    result.getProperties().put(property.getKey(), property.getValue());
                }
                else
                {
                    // If present in both merge facets of both types
                    final ObjectPropertyDefinition propertyDefinition = result.getProperties().get(property.getKey());
                    result.getProperties().put(property.getKey(), propertyDefinition.mergeFacets(property.getValue()));
                }
            }
        }
        return result;

    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
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

    public Map<String, ObjectPropertyDefinition> getProperties()
    {
        return properties;
    }

    public void setProperties(Map<String, ObjectPropertyDefinition> properties)
    {
        if (properties != null && !properties.isEmpty())
        {
            this.properties = properties;
        }
    }
}
