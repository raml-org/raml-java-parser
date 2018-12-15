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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.NamedNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;

import javax.annotation.Nonnull;

public class TypeDeclarationField extends KeyValueNodeImpl implements NamedNode
{
    public TypeDeclarationField(KeyValueNodeImpl node)
    {
        super(node);
    }

    public TypeDeclarationField()
    {
        super();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new TypeDeclarationField(this);
    }

    public String getName()
    {
        return ((SimpleTypeNode) getKey()).getLiteralValue();
    }
}
