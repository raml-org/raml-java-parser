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

import javax.annotation.Nonnull;

/**
 * Handles the binding between the model interface and NodeBaseModel implementation
 */
public interface ModelBindingConfiguration
{
    /**
     * Returns a class that implements NodeBaseModel
     * @param className The className
     * @return The factory of the implementation
     */
    @Nonnull
    NodeModelFactory bindingOf(Class<?> className);

    /**
     * Returns the reverse binding of a model in a polymorphic scenario.
     * Returns the interface class that binds to this implementation
     * {@link NodeModelFactory#polymorphic()}
     * @param model The implementation model
     * @return The interface class
     */
    @Nonnull
    Class<?> reverseBindingOf(NodeModel model);
}
