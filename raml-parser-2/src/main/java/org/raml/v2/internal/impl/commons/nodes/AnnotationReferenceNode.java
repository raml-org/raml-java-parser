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

import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.util.NodeSelector;

public class AnnotationReferenceNode extends AbstractReferenceNode implements StringNode
{

    public AnnotationReferenceNode()
    {
    }

    public AnnotationReferenceNode(AnnotationReferenceNode node)
    {
        super(node);
    }

    @Override
    public String getRefName()
    {
        String value = getValue();
        int from = value.lastIndexOf(".") + 1;
        if (from == 0)
        {
            from = 1;
        }
        return value.substring(from, value.length() - 1);
    }

    @Nullable
    @Override
    public AnnotationTypeNode resolveReference()
    {
        for (Node contextNode : getContextNodes())
        {
            final Node resolve = NodeSelector.selectFrom(Raml10Grammar.ANNOTATION_TYPES_KEY_NAME + "/*/" + getRefName() + "/..", contextNode);
            if (resolve instanceof AnnotationTypeNode)
            {
                return (AnnotationTypeNode) resolve;
            }
            if (resolve != null)
            {
                return null;
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new AnnotationReferenceNode(this);
    }

    @Override
    public String getValue()
    {
        return ((StringNode) getSource()).getValue();
    }

    @Override
    public String getLiteralValue()
    {
        return getValue();
    }

    @Override
    public String toString()
    {
        return getValue();
    }
}
