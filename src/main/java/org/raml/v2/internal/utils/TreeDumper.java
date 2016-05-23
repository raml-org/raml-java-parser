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

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.nodes.BooleanNode;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.IntegerNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.Position;
import org.raml.v2.internal.framework.nodes.ReferenceNode;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;

import java.util.Collection;


public class TreeDumper
{

    private static final int TAB_SPACES = 4;
    protected StringBuilder dump;
    private int indent = 0;

    private TreeDumper(StringBuilder dump)
    {
        this.dump = dump;
    }

    public TreeDumper()
    {
        this(new StringBuilder());
    }

    public String dump(Node node)
    {
        printIndent();
        dumpNode(node);
        dump.append(" (");
        dump.append("Start: ").append(node.getStartPosition().getIndex());
        dump.append(" , End: ").append(node.getEndPosition().getIndex());
        if (node.getStartPosition().getIndex() != Position.UNKNOWN &&
            node.getEndPosition().getIndex() != Position.UNKNOWN)
        {
            dump.append(", On: ").append(node.getStartPosition().getPath());
        }
        if (node.getSource() != null)
        {
            dump.append(", Source: ");
            dump.append(node.getSource().getClass().getSimpleName());
        }
        dump.append(")");
        dump.append("\n");
        indent();
        for (Node child : getChildren(node))
        {
            dump(child);
        }

        if (node instanceof LibraryLinkNode)
        {
            final Node refNode = ((LibraryLinkNode) node).getRefNode();
            if (refNode != null)
            {
                dump(refNode);
            }
        }
        dedent();
        return dump.toString();
    }

    private Collection<Node> getChildren(Node node)
    {
        return node.getChildren();
    }

    protected void dumpNode(Node node)
    {

        dump.append(node.getClass().getSimpleName());
        if (node instanceof StringNode)
        {
            dump.append(": \"").append(((StringNode) node).getValue()).append("\"");
        }
        if (node instanceof IntegerNode)
        {
            dump.append(": ").append(((IntegerNode) node).getValue());
        }
        if (node instanceof BooleanNode)
        {
            dump.append(": ").append(((BooleanNode) node).getValue());
        }
        else if (node instanceof ErrorNode)
        {
            dump.append(": \"").append(((ErrorNode) node).getErrorMessage()).append("\"");
        }
        else if (node instanceof ReferenceNode)
        {
            final ReferenceNode referenceNode = (ReferenceNode) node;
            final Node refNode = referenceNode.getRefNode();
            dump.append(" ").append(referenceNode.getRefName()).append(" -> {").append(refNode == null ? "null" : refNode.getClass().getSimpleName());
            if (refNode != null)
            {
                dump.append(" RefStart: ").append(refNode.getStartPosition().getIndex());
                dump.append(" , RefEnd: ").append(refNode.getEndPosition().getIndex());
            }
            dump.append("}");
        }
    }

    protected void dedent()
    {
        indent--;
    }

    protected void indent()
    {
        indent++;
    }

    protected void printIndent()
    {
        dump.append(StringUtils.repeat(" ", indent * TAB_SPACES));
    }

}
