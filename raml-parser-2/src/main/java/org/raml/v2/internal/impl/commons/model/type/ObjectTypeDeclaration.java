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
package org.raml.v2.internal.impl.commons.model.type;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.v2.internal.impl.commons.model.factory.TypeDeclarationModelFactory;
import org.raml.v2.internal.impl.v10.type.ObjectResolvedType;
import org.raml.v2.internal.impl.v10.type.PropertyFacets;
import org.raml.v2.internal.utils.ValueUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation binding of {@link org.raml.v2.api.model.v10.datamodel.ObjectTypeDeclaration}
 */
public class ObjectTypeDeclaration extends TypeDeclaration<ObjectResolvedType>
{

    public ObjectTypeDeclaration(KeyValueNode node, ObjectResolvedType objectTypeDefinition)
    {
        super(node, objectTypeDefinition);
    }


    public List<TypeDeclaration> properties()
    {
        final List<TypeDeclaration> result = new ArrayList<>();
        final Map<String, PropertyFacets> properties = getResolvedType().getProperties();
        for (PropertyFacets propertyFacets : properties.values())
        {
            result.add(new TypeDeclarationModelFactory().create(propertyFacets.getValueType()));
        }
        return result;
    }


    public Integer minProperties()
    {
        return getResolvedType().getMinProperties();
    }

    public Integer maxProperties()
    {
        return getResolvedType().getMaxProperties();
    }

    public Boolean additionalProperties()
    {
        return ValueUtils.asBoolean(getResolvedType().getAdditionalProperties(), true);
    }


    public String discriminator()
    {
        return getResolvedType().getDiscriminator();
    }


    public String discriminatorValue()
    {
        return getResolvedType().getDiscriminatorValue();
    }
}