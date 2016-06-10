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
import org.raml.v2.internal.impl.v10.type.NumberResolvedType;

import java.util.List;

public class NumberTypeDeclaration extends TypeDeclaration<NumberResolvedType>
{

    public NumberTypeDeclaration(KeyValueNode node, NumberResolvedType resolvedType)
    {
        super(node, resolvedType);
    }


    public Double minimum()
    {
        final Number minimum = getResolvedType().getMinimum();
        return minimum != null ? minimum.doubleValue() : null;
    }

    public Double maximum()
    {
        final Number maximum = getResolvedType().getMaximum();
        return maximum != null ? maximum.doubleValue() : null;
    }

    public List<Number> enumValues()
    {
        return getResolvedType().getEnums();
    }


    public String format()
    {
        return getResolvedType().getFormat();
    }


    public Double multipleOf()
    {
        final Number multiple = getResolvedType().getMultiple();
        return multiple != null ? multiple.doubleValue() : null;
    }
}