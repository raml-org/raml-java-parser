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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.api.loader.ResourceLoader;

/**
 * The position of a given node in a specific resource
 */
public interface Position
{
    int UNKNOWN = -1;

    /**
     * The offset from the begining of the file
     * @return The offset
     */
    int getIndex();

    /**
     * The line line number
     * @return line number
     */
    int getLine();

    /**
     * Column number
     * @return The column number
     */
    int getColumn();

    /**
     * Returns the absolute path of the resource where this position is relative to
     * @return The absolute path
     */
    @Nonnull
    String getPath();

    /**
     * Returns the resource URI of the current position
     * @return The resource URI if it is included, null if not
     */
    @Nullable
    String getIncludedResourceUri();

    /**
     * Sets the URI of the resource in the current position
     * @param includedResourceURI The resource URI
     */
    void setIncludedResourceUri(String includedResourceURI);

    @Nonnull
    Position rightShift(int offset);

    @Nonnull
    Position leftShift(int offset);

    @Nonnull
    ResourceLoader getResourceLoader();
}
