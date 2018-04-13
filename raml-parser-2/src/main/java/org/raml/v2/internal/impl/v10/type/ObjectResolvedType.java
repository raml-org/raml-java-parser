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

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationField;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.rule.RamlErrorNodeFactory;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.type.SchemaBasedResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.nodes.PropertyNode;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.ADDITIONAL_PROPERTIES_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.DISCRIMINATOR_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.DISCRIMINATOR_VALUE_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MAX_PROPERTIES_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MIN_PROPERTIES_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.PROPERTIES_KEY_NAME;
import static org.raml.yagi.framework.util.NodeSelector.selectBooleanValue;
import static org.raml.yagi.framework.util.NodeSelector.selectIntValue;
import static org.raml.yagi.framework.util.NodeSelector.selectStringValue;

public class ObjectResolvedType extends XmlFacetsCapableType
{
    private Integer minProperties;
    private Integer maxProperties;
    private Boolean additionalProperties;
    private String discriminator;
    private String discriminatorValue;

    private Map<String, PropertyFacets> properties = new LinkedHashMap<>();

    public ObjectResolvedType(
            String typeName, TypeExpressionNode declarationNode,
            XmlFacets xmlFacets,
            Integer minProperties,
            Integer maxProperties,
            Boolean additionalProperties,
            String discriminator,
            String discriminatorValue,
            Map<String, PropertyFacets> properties,
            ResolvedCustomFacets customFacets)
    {
        super(typeName, declarationNode, xmlFacets, customFacets);
        this.minProperties = minProperties;
        this.maxProperties = maxProperties;
        this.additionalProperties = additionalProperties;
        this.discriminator = discriminator;
        this.discriminatorValue = discriminatorValue;
        this.properties = properties;
    }

    public ObjectResolvedType(TypeExpressionNode from)
    {
        super(getTypeName(from, TypeId.OBJECT.getType()), from,
                new ResolvedCustomFacets(MIN_PROPERTIES_KEY_NAME, MAX_PROPERTIES_KEY_NAME, ADDITIONAL_PROPERTIES_KEY_NAME, DISCRIMINATOR_KEY_NAME, DISCRIMINATOR_VALUE_KEY_NAME, PROPERTIES_KEY_NAME));
    }

    protected ObjectResolvedType copy()
    {
        return new ObjectResolvedType(getTypeName(), getTypeExpressionNode(),
                getXmlFacets().copy(),
                minProperties,
                maxProperties,
                additionalProperties,
                discriminator,
                discriminatorValue,
                new LinkedHashMap<>(properties),
                customFacets.copy());
    }

    @Override
    public boolean doAccept(ResolvedType valueType)
    {
        final boolean inheritsFrom = super.doAccept(valueType);
        if (inheritsFrom && (valueType instanceof ObjectResolvedType))
        {
            final Map<String, PropertyFacets> properties = getProperties();
            for (PropertyFacets myProperty : properties.values())
            {
                final PropertyFacets matchedProperty = ((ObjectResolvedType) valueType).getProperties().get(myProperty.getName());
                if (matchedProperty != null)
                {
                    if (!myProperty.getValueType().accepts(matchedProperty.getValueType()))
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }

        }
        return inheritsFrom;
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final ObjectResolvedType result = copy();
        result.customFacets = customFacets.overwriteFacets(from);
        result.setMinProperties(selectIntValue(MIN_PROPERTIES_KEY_NAME, from));
        result.setMaxProperties(selectIntValue(MAX_PROPERTIES_KEY_NAME, from));
        result.setAdditionalProperties(selectBooleanValue(ADDITIONAL_PROPERTIES_KEY_NAME, from));
        result.setDiscriminator(selectStringValue(DISCRIMINATOR_KEY_NAME, from));
        result.setDiscriminatorValue(selectStringValue(DISCRIMINATOR_VALUE_KEY_NAME, from));
        final Node properties = from.get(PROPERTIES_KEY_NAME);
        if (properties != null)
        {
            final List<Node> children = properties.getChildren();
            for (Node child : children)
            {
                if (child instanceof PropertyNode)
                {
                    final PropertyNode property = (PropertyNode) child;
                    final String name = property.getName();
                    final PropertyFacets propertyDefinition = new PropertyFacets(property);
                    final Map<String, PropertyFacets> resultProperties = result.getProperties();
                    if (!resultProperties.containsKey(name))
                    {
                        resultProperties.put(name, propertyDefinition);
                    }
                    else
                    {
                        // If present in both merge facets of both types
                        resultProperties.put(name, resultProperties.get(name).overwriteFacets(propertyDefinition));
                        if (propertyDefinition.getErrorNode() != null)
                        {
                            child.replaceWith(propertyDefinition.getErrorNode());
                        }
                    }
                }
            }
        }
        return overwriteFacets(result, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final ObjectResolvedType result = copy();
        if (with instanceof ObjectResolvedType)
        {
            result.setMinProperties(((ObjectResolvedType) with).getMinProperties());
            result.setMaxProperties(((ObjectResolvedType) with).getMaxProperties());
            result.setAdditionalProperties(((ObjectResolvedType) with).getAdditionalProperties());
            result.setDiscriminator(((ObjectResolvedType) with).getDiscriminator());
            result.setDiscriminatorValue(((ObjectResolvedType) with).getDiscriminatorValue());
            final Map<String, PropertyFacets> properties = ((ObjectResolvedType) with).getProperties();
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
        result.customFacets = result.customFacets.mergeWith(with.customFacets());
        return mergeFacets(result, with);

    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {

        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.propertiesField())
                                                   .add(raml10Grammar.minPropertiesField())
                                                   .add(raml10Grammar.maxPropertiesField())
                                                   .add(raml10Grammar.additionalPropertiesField())
                                                   .addAll(customFacets.getRules());

        if (from.getParent() instanceof TypeDeclarationField && from.getResolvedType() instanceof ObjectResolvedType)
        {
            facetRule.add(raml10Grammar.discriminatorField())
                     .add(raml10Grammar.discriminatorValueField());
        }

        TypesUtils.validateAllWith(facetRule, from.getFacets());
        final Node properties = from.get(PROPERTIES_KEY_NAME);
        if (properties != null)
        {
            final List<Node> children = properties.getChildren();
            for (Node child : children)
            {
                if (child instanceof PropertyNode)
                {
                    final PropertyNode property = (PropertyNode) child;
                    final String name = property.getName();
                    if (this.properties.containsKey(name))
                    {
                        PropertyFacets myProperty = this.properties.get(name);
                        if (!myProperty.getValueType().accepts(property.getTypeDefinition()))
                        {
                            property.replaceWith(RamlErrorNodeFactory.createCanNotOverrideProperty(name));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void validateState()
    {
        super.validateState();
        final ErrorNode errorNode = validateFacets();
        if (errorNode != null)
        {
            getTypeExpressionNode().replaceWith(errorNode);
        }
    }

    public ErrorNode validateFacets()
    {
        int min = minProperties != null ? minProperties : 0;
        int max = maxProperties != null ? maxProperties : Integer.MAX_VALUE;
        if (max < min)
        {
            return RamlErrorNodeFactory.createInvalidFacetState(getTypeName(), "maxProperties must be greater than or equal to minProperties");
        }
        for (PropertyFacets propertyFacets : properties.values())
        {
            if (propertyFacets.getValueType() instanceof SchemaBasedResolvedType)
            {
                return RamlErrorNodeFactory.createPropertyCanNotBeOfSchemaType(propertyFacets.getName());
            }
        }

        if (discriminator != null)
        {
            if (this.properties.get(discriminator) == null)
            {
                return RamlErrorNodeFactory.createInvalidFacetState(getTypeName(), "invalid discriminator value, property '" + discriminator + "' does not exist");
            }
        }
        return null;
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
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

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.OBJECT.getType();
    }

}
