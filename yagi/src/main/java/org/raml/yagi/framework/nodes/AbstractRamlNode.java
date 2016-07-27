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

public abstract class AbstractRamlNode extends BaseNode
{

    public AbstractRamlNode()
    {
    }

    public AbstractRamlNode(AbstractRamlNode node)
    {
        super(node);
        this.startPosition = node.startPosition;
        this.endPosition = node.endPosition;
    }

    @Nullable
    private Position endPosition;

    @Nullable
    private Position startPosition;

    public void setEndPosition(@Nullable Position endPosition)
    {
        this.endPosition = endPosition;
    }

    public void setStartPosition(@Nullable Position startPosition)
    {
        this.startPosition = startPosition;
    }

    @Override
    public void setSource(Node source)
    {
        super.setSource(source);
        this.endPosition = source.getEndPosition();
        this.startPosition = source.getStartPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return endPosition != null ? endPosition : DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return startPosition != null ? startPosition : DefaultPosition.emptyPosition();
    }
}
