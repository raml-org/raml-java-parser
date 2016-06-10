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
package org.raml.yagi.framework.model;

import org.raml.yagi.framework.nodes.Node;

/**
 * Create a Node based model based on a Node
 */
public interface NodeModelFactory
{
    /**
     * The model to create
     * @param node The node
     * @return The model
     */
    NodeModel create(Node node);

    /**
     * True if this factory create polymorphic implementations
     * @return If the model is polymorphic
     */
    boolean polymorphic();
}
