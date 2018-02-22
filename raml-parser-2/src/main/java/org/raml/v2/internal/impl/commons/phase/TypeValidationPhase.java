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
package org.raml.v2.internal.impl.commons.phase;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.ResolvedType;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.util.NodeUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TypeValidationPhase implements Phase
{

    @Override
    public Node apply(Node tree)
    {
        final List<TypeDeclarationNode> descendantsWith = tree.findDescendantsWith(TypeDeclarationNode.class);
        for (TypeDeclarationNode typeDeclarationNode : descendantsWith)
        {
            if (!NodeUtils.isErrorResult(typeDeclarationNode))
            {
                if (validateInheritFromValidTypes(typeDeclarationNode))
                {
                    typeDeclarationNode.validateCanOverwrite();
                    typeDeclarationNode.validateState();
                }
            }
        }
        return tree;
    }

    private boolean validateInheritFromValidTypes(TypeDeclarationNode typeDeclarationNode)
    {
        List<TypeExpressionNode> baseTypes = typeDeclarationNode.getBaseTypes();
        Set<String> extendedTypes = new HashSet<>();
        for (TypeExpressionNode baseType : baseTypes)
        {
            ResolvedType resolvedType = baseType.generateDefinition();
            if (resolvedType != null)
            {
                if (resolvedType.getBuiltinTypeName() != null)
                {
                    extendedTypes.add(resolvedType.getBuiltinTypeName());
                }
            }
        }
        if (extendedTypes.size() > 1)
        {
            typeDeclarationNode.replaceWith(ErrorNodeFactory.createCanNotInheritFromDifferentBaseTypes(extendedTypes.toArray(new String[0])));
            return false;
        }
        return true;
    }

}
