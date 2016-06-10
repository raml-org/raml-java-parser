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
package org.raml.yagi.framework.nodes;

import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nonnull;

/**
 * Defines a scope for all its children where to search references.
 */
public interface ContextProviderNode
{
    /**
     * Returns the node that defines the naming reference scope for this node.
     * @return The context node for this node.
     */
    @Nonnull
    Node getContextNode();
}
