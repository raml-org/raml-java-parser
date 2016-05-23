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
package org.raml.v2.internal.impl.commons.type;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.v10.type.TypeDefinitionVisitor;

public class XmlSchemaTypeDefinition extends BaseTypeDefinition
{
    private final String schemaValue;
    private final String schemaPath;
    private final String internalFragment;

    public XmlSchemaTypeDefinition(String schemaValue, String schemaPath, String internalFragment)
    {

        this.schemaValue = schemaValue;
        this.schemaPath = schemaPath;
        this.internalFragment = internalFragment;
    }

    protected XmlSchemaTypeDefinition copy()
    {
        return new XmlSchemaTypeDefinition(schemaValue, schemaPath, internalFragment);
    }

    @Override
    public TypeDefinition overwriteFacets(TypeDeclarationNode from)
    {
        return copy();
    }

    @Override
    public TypeDefinition mergeFacets(TypeDefinition with)
    {
        return copy();
    }

    @Override
    public <T> T visit(TypeDefinitionVisitor<T> visitor)
    {
        return visitor.visitXml(this);
    }

    public String getSchemaValue()
    {
        return schemaValue;
    }

    public String getSchemaPath()
    {
        return schemaPath;
    }

    public String getInternalFragment()
    {
        return internalFragment;
    }
}
