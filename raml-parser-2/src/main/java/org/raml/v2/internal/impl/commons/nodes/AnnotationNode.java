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

import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;

/**
 * An annotation usage
 */
public class AnnotationNode extends KeyValueNodeImpl implements OverlayableNode
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
        AnnotationReferenceNode node = null;
        if (super.getKey() instanceof AnnotationReferenceNode)
        {
            node = (AnnotationReferenceNode) super.getKey();
        }
        return node;
    }

    public AnnotationTarget getTarget()
    {
        Node parent = getParent();
        Node grampa = parent != null ? parent.getParent() : null;
        if (parent instanceof RamlDocumentNode)
        {
            return AnnotationTarget.API;
        }
        if (parent instanceof DocumentationItemNode)
        {
            return AnnotationTarget.DocumentationItem;
        }
        if (grampa instanceof ResourceNode)
        {
            return AnnotationTarget.Resource;
        }
        if (grampa instanceof MethodNode)
        {
            return AnnotationTarget.Method;
        }
        if (grampa instanceof BodyNode)
        {
            if (grampa.findAncestorWith(ResponseNode.class) != null)
            {
                return AnnotationTarget.ResponseBody;
            }
            return AnnotationTarget.RequestBody;
        }
        if (parent instanceof TypeDeclarationNode)
        {
            return AnnotationTarget.TypeDeclaration;
        }
        if (grampa instanceof ResponseNode)
        {
            return AnnotationTarget.Response;
        }
        if (grampa instanceof SecuritySchemeNode)
        {
            return AnnotationTarget.SecurityScheme;
        }
        if (isSecuritySchemeSettings(grampa))
        {
            return AnnotationTarget.SecuritySchemeSettings;
        }
        return null;
    }

    private boolean isSecuritySchemeSettings(Node grampa)
    {
        boolean result = false;
        if (grampa instanceof KeyValueNode)
        {
            Node key = ((KeyValueNode) grampa).getKey();
            if (key instanceof StringNode && "settings".equals(((StringNode) key).getValue()))
            {
                result = grampa.findAncestorWith(SecuritySchemeNode.class) != null;
            }
        }
        return result;
    }
}
