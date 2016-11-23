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
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.v10.type.TypeVisitor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a resolved type in the type system.
 * It describes all the facets of a type.
 */
public interface ResolvedType
{
    /**
     * Returns a new type definition with the facets being overwritten from the specified node
     * @param from The node from where to load the facets
     * @return A new type
     */
    ResolvedType overwriteFacets(TypeDeclarationNode from);

    /**
     * Returns a new type definition after merging the facets of both Types
     * @param with The type definition to merge with
     * @return The new type
     */
    ResolvedType mergeFacets(ResolvedType with);

    ResolvedType setTypeNode(TypeExpressionNode typeNode);

    /**
     * Dispatch the implementation to the visitor method
     * @param visitor The visitor
     * @param <T> The result type
     * @return The result of the visitor execution
     */
    <T> T visit(TypeVisitor<T> visitor);

    /**
     * Returns the type name if any
     * @return The type name
     */
    @Nullable
    String getTypeName();

    /**
     * Returns the builtin type name.
     *  returns null for builtin types without explicit names
     *   (xsd, json schema, any, unions)
     * @return The builtin type name
     */
    @Nullable
    String getBuiltinTypeName();

    /**
     * The type declaration node that define this type
     * @return The node
     */
    TypeExpressionNode getTypeDeclarationNode();

    /**
     * Validate state consistency
     */
    void validateState();

    /**
     * Validate if this type can be overwritten by the specified node
     * @param from The node to check with
     */
    void validateCanOverwriteWith(TypeDeclarationNode from);

    /**
     * Returns the custom facets definitions of this type
     * @return The custom facets definition.
     */
    @Nonnull
    ResolvedCustomFacets customFacets();

    boolean accepts(ResolvedType valueType);
}
