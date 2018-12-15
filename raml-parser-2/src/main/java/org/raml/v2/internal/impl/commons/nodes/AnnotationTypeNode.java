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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.NamedNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.util.NodeSelector;

public class AnnotationTypeNode extends KeyValueNodeImpl implements NamedNode
{

    public AnnotationTypeNode()
    {
    }

    public AnnotationTypeNode(AnnotationTypeNode node)
    {
        super(node);
    }

    public String getName()
    {
        final StringNode key = (StringNode) getKey();
        return key.getValue();
    }

    public TypeDeclarationNode getDeclaredType()
    {
        return (TypeDeclarationNode) getValue();
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new AnnotationTypeNode(this);
    }

    public List<AnnotationTarget> getAllowedTargets()
    {
        List<AnnotationTarget> targets = new ArrayList<>();
        List<String> allowedTargets = NodeSelector.selectStringCollection("allowedTargets", getValue());
        for (String allowedTarget : allowedTargets)
        {
            targets.add(AnnotationTarget.valueOf(allowedTarget));
        }
        return targets;
    }
}
