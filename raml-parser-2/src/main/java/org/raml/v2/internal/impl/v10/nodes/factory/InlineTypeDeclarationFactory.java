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
package org.raml.v2.internal.impl.v10.nodes.factory;

import org.raml.yagi.framework.grammar.rule.NodeFactory;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nonnull;

public class InlineTypeDeclarationFactory implements NodeFactory
{

    @Override
    public TypeDeclarationNode create(@Nonnull Node currentNode, Object... args)
    {
        final TypeDeclarationNode node = new TypeDeclarationNode();
        node.addChild(new KeyValueNodeImpl(new StringNodeImpl("type"), currentNode.copy()));
        // We remove the children as they where already copied
        currentNode.removeChildren();
        return node;
    }


}
