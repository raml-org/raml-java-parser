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

import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;

import java.util.List;

import static org.raml.v2.internal.utils.NodeSelector.selectIntValue;
import static org.raml.v2.internal.utils.NodeSelector.selectStringCollection;

public class FileResolvedType extends XmlFacetsCapableType
{

    private Number minLength;
    private Number maxLength;
    private List<String> fileTypes;

    public FileResolvedType()
    {
    }

    public FileResolvedType(TypeDeclarationNode declarationNode, XmlFacets xmlFacets, Number minLength, Number maxLength, List<String> fileTypes)
    {
        super(declarationNode, xmlFacets);
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.fileTypes = fileTypes;
    }

    protected FileResolvedType copy()
    {
        return new FileResolvedType(getTypeDeclarationNode(), getXmlFacets().copy(), minLength, maxLength, fileTypes);
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        final FileResolvedType result = copy();
        result.setMinLength(selectIntValue("minLength", from));
        result.setMaxLength(selectIntValue("maxLength", from));
        result.setFileTypes(selectStringCollection("fileTypes", from));
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
        return mergeFacets(result, with);
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitFile(this);
    }

    public Number getMinLength()
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

    public void setMinLength(Number minLength)
    {
        if (minLength != null)
        {
            this.minLength = minLength;
        }
    }

    public Number getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(Number maxLength)
    {
        if (maxLength != null)
        {
            this.maxLength = maxLength;
        }
    }
}
