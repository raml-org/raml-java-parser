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

import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.impl.v10.nodes.types.builtin.ObjectTypeNode;
import org.raml.v2.internal.framework.nodes.BooleanNode;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.IntegerNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.ReferenceNode;
import org.raml.v2.internal.framework.nodes.StringNode;


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
        dedent();
        return dump.toString();
    }

    private Collection<Node> getChildren(Node node)
    {
        Collection<Node> children = node.getChildren();
        if (node instanceof ObjectTypeNode)
        {
            List<Node> merged = Lists.newArrayList();
            merged.addAll(children);
            merged.addAll(((ObjectTypeNode) node).getInheritedProperties());
            children = merged;
        }
        return children;
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
            final Node refNode = ((ReferenceNode) node).getRefNode();
            dump.append(" -> {").append(refNode == null ? "null" : refNode.getClass().getSimpleName());
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
