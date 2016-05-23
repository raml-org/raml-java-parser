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
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYArrayNode;
import org.raml.v2.internal.impl.commons.type.TypeDefinition;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;
import static org.raml.v2.internal.utils.NodeSelector.selectStringValue;

public class StringTypeDefinition implements TypeDefinition
{
    private Integer minLength;
    private Integer maxLength;
    private String pattern;
    private List<String> enums;

    public StringTypeDefinition()
    {
    }

    public StringTypeDefinition(Integer minLength, Integer maxLength, String pattern, List<String> enums)
    {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.pattern = pattern;
        this.enums = enums;
    }

    protected StringTypeDefinition copy()
    {
        return new StringTypeDefinition(minLength, maxLength, pattern, enums);
    }


    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        final StringTypeDefinition result = copy();
        result.setMinLength(selectIntValue("minLength", from));
        result.setMaxLength(selectIntValue("maxLength", from));
        result.setPattern(selectStringValue("pattern", from));
        result.setEnums(getEnumValues(from));
        return result;
    }

    @Nonnull
    private List<String> getEnumValues(Node typeNode)
    {

        Node values = typeNode.get("enum");
        List<String> enumValues = new ArrayList<>();
        if (values != null && values instanceof SYArrayNode)
        {
            for (Node node : values.getChildren())
            {
                enumValues.add(((StringNode) node).getValue());
            }
        }
        return enumValues;
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        final StringTypeDefinition result = copy();
        if (with instanceof StringTypeDefinition)
        {
            final StringTypeDefinition stringTypeDefinition = (StringTypeDefinition) with;
            result.setMaxLength(stringTypeDefinition.getMaxLength());
            result.setMinLength(stringTypeDefinition.getMinLength());
            result.setPattern(stringTypeDefinition.getPattern());
            result.setEnums(stringTypeDefinition.getEnums());
        }
        return result;
    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
    {
        return visitor.visitString(this);
    }

    private void setPattern(String pattern)
    {
        if (pattern != null)
        {
            this.pattern = pattern;
        }
    }

    private void setMinLength(Integer minLength)
    {
        if (minLength != null)
        {
            this.minLength = minLength;
        }
    }

    private void setMaxLength(Integer maxLength)
    {
        if (maxLength != null)
        {
            this.maxLength = maxLength;
        }
    }

    public List<String> getEnums()
    {
        return enums;
    }

    public void setEnums(List<String> enums)
    {
        if (enums != null && !enums.isEmpty())
        {
            this.enums = enums;
        }
    }

    @Nullable
    public Integer getMinLength()
    {
        return minLength;
    }

    @Nullable
    public Integer getMaxLength()
    {
        return maxLength;
    }

    @Nullable
    public String getPattern()
    {
        return pattern;
    }
}
