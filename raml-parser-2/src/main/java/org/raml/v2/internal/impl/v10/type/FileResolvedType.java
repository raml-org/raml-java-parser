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

import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.FORMAT_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MAX_LENGTH_KEY_NAME;
import static org.raml.v2.internal.impl.v10.grammar.Raml10Grammar.MIN_LENGTH_KEY_NAME;
import static org.raml.yagi.framework.util.NodeSelector.selectIntValue;
import static org.raml.yagi.framework.util.NodeSelector.selectStringCollection;

import java.util.List;

import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.rules.TypesUtils;
import org.raml.yagi.framework.grammar.rule.AnyOfRule;

public class FileResolvedType extends XmlFacetsCapableType
{

    private Integer minLength;
    private Integer maxLength;
    private List<String> fileTypes;

    public FileResolvedType(TypeExpressionNode from)
    {
        super(from, new ResolvedCustomFacets(MIN_LENGTH_KEY_NAME, MAX_LENGTH_KEY_NAME, FORMAT_KEY_NAME));
    }

    public FileResolvedType(TypeExpressionNode declarationNode, XmlFacets xmlFacets, Integer minLength, Integer maxLength, List<String> fileTypes, ResolvedCustomFacets customFacets)
    {
        super(declarationNode, xmlFacets, customFacets);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.fileTypes = fileTypes;
    }

    protected FileResolvedType copy()
    {
        return new FileResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), minLength, maxLength, fileTypes, customFacets.copy());
    }

    @Override
    public void validateCanOverwriteWith(TypeDeclarationNode from)
    {
        customFacets.validate(from);
        final Raml10Grammar raml10Grammar = new Raml10Grammar();
        final AnyOfRule facetRule = new AnyOfRule()
                                                   .add(raml10Grammar.fileTypesField())
                                                   .add(raml10Grammar.minLengthField())
                                                   .add(raml10Grammar.maxLengthField())
                                                   .addAll(customFacets.getRules());
        TypesUtils.validateAllWith(facetRule, from.getFacets());
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final FileResolvedType result = copy();
        result.customFacets = customFacets.overwriteFacets(from);
        result.setMinLength(selectIntValue(MIN_LENGTH_KEY_NAME, from));
        result.setMaxLength(selectIntValue(MAX_LENGTH_KEY_NAME, from));
        result.setFileTypes(selectStringCollection(FORMAT_KEY_NAME, from));
        return overwriteFacets(result, from);
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        final FileResolvedType result = copy();

        if (with instanceof FileResolvedType)
        {
            FileResolvedType fileTypeDefinition = (FileResolvedType) with;
            result.setMinLength(fileTypeDefinition.getMinLength());
            result.setMaxLength(fileTypeDefinition.getMaxLength());
            result.setFileTypes(fileTypeDefinition.getFileTypes());
        }
        result.customFacets = result.customFacets.mergeWith(with.customFacets());
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitFile(this);
    }

    public Integer getMinLength()
    {
        return minLength;
    }

    public List<String> getFileTypes()
    {
        return fileTypes;
    }

    public void setFileTypes(List<String> fileTypes)
    {
        if (fileTypes != null && !fileTypes.isEmpty())
        {
            this.fileTypes = fileTypes;
        }
    }

    public void setMinLength(Integer minLength)
    {
        if (minLength != null)
        {
            this.minLength = minLength;
        }
    }

    public Integer getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength)
    {
        if (maxLength != null)
        {
            this.maxLength = maxLength;
        }
    }

    @Nullable
    @Override
    public String getBuiltinTypeName()
    {
        return TypeId.FILE.getType();
    }

}
