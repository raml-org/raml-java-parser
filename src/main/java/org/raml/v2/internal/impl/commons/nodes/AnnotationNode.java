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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.raml.v2.internal.framework.nodes.KeyValueNodeImpl;
import org.raml.v2.internal.framework.nodes.Node;

/**
 * An annotation usage
 */
public class AnnotationNode extends KeyValueNodeImpl
{

    public AnnotationNode()
    {
    }

    public AnnotationNode(AnnotationNode node)
    {
        super(node);
    }

    public String getName()
    {
        return getKey().getRefName();
    }

    @Nullable
    public AnnotationTypeNode getAnnotationTypeNode()
    {
        AnnotationReferenceNode key = getKey();
        return (AnnotationTypeNode) key.getRefNode();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new AnnotationNode(this);
    }

    @Override
    public AnnotationReferenceNode getKey()
    {
        return (AnnotationReferenceNode) super.getKey();
    }
}
