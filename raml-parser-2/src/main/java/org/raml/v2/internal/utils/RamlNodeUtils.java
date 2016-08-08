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
package org.raml.v2.internal.utils;

import org.raml.v2.internal.impl.commons.RamlVersion;
import org.raml.v2.internal.impl.commons.nodes.RamlVersionAnnotation;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeAnnotation;
import org.raml.yagi.framework.util.NodeUtils;

import java.util.ArrayList;
import java.util.List;

public class RamlNodeUtils
{
    public static boolean isErrorResult(Node node)
    {
        if (NodeUtils.isErrorResult(node))
        {
            return true;
        }
        else if (node != null)
        {
            final List<LibraryLinkNode> descendantsWith = node.findDescendantsWith(LibraryLinkNode.class);
            for (LibraryLinkNode libraryLinkNode : descendantsWith)
            {
                if (isErrorResult(libraryLinkNode.getRefNode()))
                {
                    return true;
                }
            }
        }
        return false;
    }


    public static List<ErrorNode> getErrors(Node node)
    {
        List<ErrorNode> result = new ArrayList<>();
        if (node != null)
        {
            if (node instanceof ErrorNode)
            {
                result.add((ErrorNode) node);
            }

            result.addAll(node.findDescendantsWith(ErrorNode.class));


            final List<LibraryLinkNode> descendantsWith = node.findDescendantsWith(LibraryLinkNode.class);
            for (LibraryLinkNode libraryLinkNode : descendantsWith)
            {
                result.addAll(getErrors(libraryLinkNode.getRefNode()));
            }
        }
        return result;
    }

    public static RamlVersion getVersion(Node node)
    {
        while (true)
        {
            for (NodeAnnotation annotation : node.annotations())
            {
                if (annotation instanceof RamlVersionAnnotation)
                {
                    return ((RamlVersionAnnotation) annotation).getVersion();
                }
            }
            node = node.getParent();
            if (node == null)
            {
                throw new RuntimeException("Raml Version not specified.");
            }
        }
    }
}
