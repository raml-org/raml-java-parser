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
package org.raml.yagi.framework.util;

import java.util.Collection;

import org.apache.commons.lang.StringUtils;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNode;


public class TreeNodeDumper
{

    private static final int TAB_SPACES = 4;
    protected StringBuilder dump;
    private int indent = 0;

    private boolean dumpOn = true;

    private TreeNodeDumper(StringBuilder dump)
    {
        this.dump = dump;
    }

    public TreeNodeDumper()
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
            node.getEndPosition().getIndex() != Position.UNKNOWN && dumpOn)
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
        dumpChildren(node);
        dedent();
        return dump.toString();
    }

    protected void dumpChildren(Node node)
    {
        for (Node child : getChildren(node))
        {
            dump(child);
        }
    }

    public TreeNodeDumper dumpOn(boolean dumpOn)
    {
        this.dumpOn = dumpOn;
        return this;
    }

    protected Collection<Node> getChildren(Node node)
    {
        return node.getChildren();
    }

    protected void dumpNode(Node node)
    {

        dump.append(node.getClass().getSimpleName());
        if (node instanceof ReferenceNode)
        {
            dumpReference((ReferenceNode) node);
        }
        else if (node instanceof StringNode)
        {
            dump.append(": \"").append(((StringNode) node).getValue()).append("\"");
        }
        else if (node instanceof SimpleTypeNode)
        {
            dump.append(": ").append(((SimpleTypeNode) node).getValue());
        }
        else if (node instanceof ErrorNode)
        {
            dump.append(": \"").append(((ErrorNode) node).getErrorMessage()).append("\"");
        }
    }

    private void dumpReference(ReferenceNode node)
    {
        final ReferenceNode referenceNode = node;
        final Node refNode = referenceNode.getRefNode();
        dump.append(" ").append(referenceNode.getRefName()).append(" -> {").append(refNode == null ? "null" : refNode.getClass().getSimpleName());
        if (refNode != null)
        {
            dump.append(" RefStart: ").append(refNode.getStartPosition().getIndex());
            dump.append(" , RefEnd: ").append(refNode.getEndPosition().getIndex());
        }
        dump.append("}");
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
