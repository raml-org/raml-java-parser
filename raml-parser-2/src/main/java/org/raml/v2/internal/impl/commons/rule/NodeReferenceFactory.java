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
package org.raml.v2.internal.impl.commons.rule;

import org.raml.v2.internal.impl.commons.nodes.*;
import org.raml.v2.internal.impl.v10.nodes.LibraryNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryRefNode;
import org.raml.yagi.framework.grammar.rule.ClassNodeFactory;
import org.raml.yagi.framework.grammar.rule.NodeFactory;
import org.raml.yagi.framework.nodes.AbstractRamlNode;
import org.raml.yagi.framework.nodes.NamedNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;
import org.raml.yagi.framework.util.NodeSelector;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeReferenceFactory implements NodeFactory
{

    private static final Map<String, Class<? extends NamedNode>> DECLARATION_SECTIONS;
    static
    {
        DECLARATION_SECTIONS = new HashMap<>();
        DECLARATION_SECTIONS.put("types", TypeDeclarationField.class);
        DECLARATION_SECTIONS.put("schemas", TypeDeclarationField.class);
        DECLARATION_SECTIONS.put("resourceTypes", ResourceTypeNode.class);
        DECLARATION_SECTIONS.put("traits", TraitNode.class);
        DECLARATION_SECTIONS.put("securitySchemes", SecuritySchemeNode.class);
        DECLARATION_SECTIONS.put("annotationTypes", AnnotationTypeNode.class);
    }

    private NodeFactory defaultFactory;

    public NodeReferenceFactory(Class<? extends Node> referenceClassNode)
    {
        defaultFactory = new ClassNodeFactory(referenceClassNode);
    }

    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        final String value = (String) args[0];
        return parse(currentNode, value, 0);
    }

    public Node parse(Node currentNode, String value, int startLocation)
    {

        final String[] parts = getParts(currentNode, value);

        if (parts.length > 2)
        {
            return RamlErrorNodeFactory.createInvalidLibraryChaining(value);
        }
        Node result = null;
        Node parent = null;
        int currentShift = value.length();
        for (int i = parts.length - 1; i >= 0; i--)
        {
            String part = parts[i];
            currentShift -= part.length();
            final Position endPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift + value.length());
            final Position startPosition = currentNode.getStartPosition().rightShift(startLocation + currentShift);
            if (parent == null)
            {
                parent = defaultFactory.create(currentNode, part);
                if (parent instanceof AbstractRamlNode)
                {
                    ((AbstractRamlNode) parent).setStartPosition(startPosition);
                    ((AbstractRamlNode) parent).setEndPosition(endPosition);
                }
                result = parent;
            }
            else
            {
                final LibraryRefNode libraryRefNode = new LibraryRefNode(part);
                libraryRefNode.setStartPosition(startPosition);
                libraryRefNode.setEndPosition(endPosition);
                parent.addChild(libraryRefNode);
                parent = libraryRefNode;
                // The 1 is from the dot
                currentShift -= 1;
            }
            if (i == 0)
            {
                if (currentNode instanceof ContextAwareStringNodeImpl && parent instanceof AbstractReferenceNode)
                {
                    ((AbstractReferenceNode) parent).setContextNode(((ContextAwareNode) currentNode).getReferenceContext());
                }
            }
        }
        return result;
    }

    private String[] getParts(Node currentNode, String value)
    {
        if (existsDeclaration(currentNode, value))
            return new String[] {value};

        final String[] parts = value.split("\\.");

        final Node libraryDeclarationsNode = NodeSelector.selectFrom("uses", currentNode.getRootNode());
        if (libraryDeclarationsNode != null)
        {
            final List<LibraryNode> descendants = libraryDeclarationsNode.findDescendantsWith(LibraryNode.class);
            String libraryName = "";
            for (int i = 0; i < parts.length - 1; i++)
            {
                libraryName = i == 0 ? parts[i] : libraryName + "." + parts[i];
                for (LibraryNode descendant : descendants)
                {
                    final String declarationName = value.substring(libraryName.length());
                    if (descendant.getName().equalsIgnoreCase(libraryName) && existsDeclaration(descendant, declarationName))
                    {
                        return new String[] {libraryName, declarationName};
                    }
                }
            }
        }

        return parts;
    }

    private boolean existsDeclaration(Node currentNode, String value)
    {
        for (Map.Entry<String, Class<? extends NamedNode>> declarationSection : DECLARATION_SECTIONS.entrySet())
        {
            final Node node = NodeSelector.selectFrom(declarationSection.getKey(), currentNode.getRootNode());

            if (node == null)
                continue;

            final List<? extends NamedNode> descendants = node.findDescendantsWith(declarationSection.getValue());
            for (NamedNode descendant : descendants)
            {
                if (descendant.getName().equalsIgnoreCase(value))
                    return true;
            }
        }
        return false;
    }
}
