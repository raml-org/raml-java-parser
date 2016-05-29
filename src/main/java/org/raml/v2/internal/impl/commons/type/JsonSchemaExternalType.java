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
import org.raml.v2.internal.impl.v10.type.TypeVisitor;

public class JsonSchemaExternalType extends BaseType implements SchemaBasedResolvedType
{

    private final String schemaValue;
    private final String schemaPath;
    private final String internalFragment;

    public JsonSchemaExternalType(String schemaValue, String schemaPath, String internalFragment)
    {
        this.schemaValue = schemaValue;
        this.schemaPath = schemaPath;
        this.internalFragment = internalFragment;
    }

    protected JsonSchemaExternalType copy()
    {
        return new JsonSchemaExternalType(schemaValue, schemaPath, internalFragment);
    }

    @Override
    public ResolvedType overwriteFacets(TypeDeclarationNode from)
    {
        setTypeNode(from);
        return copy();
    }

    @Override
    public ResolvedType mergeFacets(ResolvedType with)
    {
        return copy();
    }

    @Override
    public <T> T visit(TypeVisitor<T> visitor)
    {
        return visitor.visitJson(this);
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
