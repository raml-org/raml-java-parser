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
package org.raml.v2.internal.impl.v10.rules;

import javax.annotation.Nullable;

import org.raml.v2.internal.impl.v10.nodes.NativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.nodes.OverridableNativeTypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.TypeId;
import org.raml.yagi.framework.grammar.rule.DefaultValue;
import org.raml.yagi.framework.nodes.Node;

public class TypeDefaultValue implements DefaultValue
{
    private TypeId defaultType;

    public TypeDefaultValue(TypeId defaultType)
    {
        this.defaultType = defaultType;
    }

    @Nullable
    @Override
    public Node getDefaultValue(Node parent)
    {
        if (parent.get("properties") != null)
        {
            return new NativeTypeExpressionNode(TypeId.OBJECT.getType());
        }
        return new OverridableNativeTypeExpressionNode(defaultType.getType());
    }
}
