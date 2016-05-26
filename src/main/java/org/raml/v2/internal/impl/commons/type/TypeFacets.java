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
import org.raml.v2.internal.impl.v10.type.TypeFacetsVisitor;

/**
 * Represents a resolved type in the type system.
 * It describes all the facets of a type.
 */
public interface TypeFacets
{
    /**
     * Returns a new type definition with the facets being overwritten from the specified node
     * @param from The node from where to load the facets
     * @return A new type definition
     */
    TypeFacets overwriteFacets(TypeDeclarationNode from);

    /**
     * Returns a new type definition after merging the facets of both TypeDefinitions
     * @param with The type definition to merge with
     * @return The new type definition
     */
    TypeFacets mergeFacets(TypeFacets with);

    /**
     * Dispatch the implementation to the visitor method
     * @param visitor The visitor
     * @param <T> The result type
     * @return The result of the visitor execution
     */
    <T> T visit(TypeFacetsVisitor<T> visitor);
}
