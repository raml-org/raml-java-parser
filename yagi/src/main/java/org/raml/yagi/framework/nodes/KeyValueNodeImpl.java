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

public class KeyValueNodeImpl extends BaseNode implements KeyValueNode
{
    private Position startPosition;
    private Position endPosition;

    public KeyValueNodeImpl()
    {
    }

    public KeyValueNodeImpl(@Nonnull Node keyNode, @Nonnull Node valueNode)
    {
        addChild(keyNode);
        addChild(valueNode);
    }

    public KeyValueNodeImpl(KeyValueNodeImpl node)
    {
        super(node);
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        if (startPosition == null)
        {
            if (getChildren().size() != 2)
            {
                return DefaultPosition.emptyPosition();
            }
            else
            {
                return getChildren().get(0).getStartPosition();
            }
        }
        else
        {
            return startPosition;
        }
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        if (endPosition == null)
        {
            if (getChildren().size() != 2)
            {
                return DefaultPosition.emptyPosition();
            }
            else
            {
                return getValue().getEndPosition();
            }
        }
        else
        {
            return endPosition;
        }
    }

    public void setStartPosition(Position startPosition)
    {
        this.startPosition = startPosition;
    }

    public void setEndPosition(Position endPosition)
    {
        this.endPosition = endPosition;
    }

    @Override
    public void addChild(Node node)
    {
        if (getChildren().size() >= 2)
        {
            throw new IllegalStateException("Can not add '" + node + "' to " + this + " node. It already has key and value.");
        }
        super.addChild(node);
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new KeyValueNodeImpl(this);
    }

    @Override
    public Node getKey()
    {
        if (getChildren().isEmpty())
        {
            throw new IllegalStateException("Key value pair with no key " + getClass().getSimpleName());
        }
        return getChildren().get(0);
    }

    @Override
    public Node getValue()
    {
        if (getChildren().size() < 2)
        {
            throw new IllegalStateException("Key value pair with no value " + getClass().getSimpleName());
        }
        return getChildren().get(1);
    }

    @Override
    public void setValue(Node valueNode)
    {
        setChild(1, valueNode);
    }

    @Override
    public String toString()
    {
        return String.format("%s: %s", getKey(), getValue());
    }


    @Override
    public NodeType getType()
    {
        return NodeType.KeyValue;
    }
}
