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

import org.apache.commons.lang.StringUtils;
import org.raml.yagi.framework.nodes.*;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.IdentityHashMap;


public class FasterTreeNodeDumper
{

    private static final int TAB_SPACES = 4;
    protected PrintWriter dump;
    private int indent = 0;
    private boolean dumpOn = true;

    private FasterTreeNodeDumper(PrintWriter dump)
    {
        this.dump = dump;
    }

    public FasterTreeNodeDumper()
    {
        this(new PrintWriter(new OutputStreamWriter(System.out)));
    }

    public String dump(Node node)
    {
        printIndent();
        dumpNode(node);
        dump.print(" (");
        dump.print("Start: " + node.getStartPosition().getIndex());
        dump.print(" , End: " + node.getEndPosition().getIndex());
        if (node.getStartPosition().getIndex() != Position.UNKNOWN &&
            node.getEndPosition().getIndex() != Position.UNKNOWN && dumpOn)
        {
            dump.print(", On: " + node.getStartPosition().getPath());
        }
        if (node.getSource() != null)
        {
            dump.print(", Source: ");
            dump.print(node.getSource().getClass().getSimpleName());
        }
        dump.print(")");
        dump.print("\n");
        indent();
        dumpChildren(node);
        dedent();
        return "";
    }

    protected void dumpChildren(Node node)
    {
        for (Node child : getChildren(node))
        {
            dump(child);
        }
    }

    public FasterTreeNodeDumper dumpOn(boolean dumpOn)
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
            dump.append(": \"").append(((StringNode) node).getValue().replace("\n", "\\n")).append("\"");
        }
        else if (node instanceof SimpleTypeNode)
        {
            dump.append(": ").print(((SimpleTypeNode) node).getValue());
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
        dump.print(" " + referenceNode.getRefName() + " -> {" + ((refNode == null ? "null" : refNode.getClass().getSimpleName())));
        if (refNode != null)
        {
            dump.append(" RefStart: " + refNode.getStartPosition().getIndex());
            dump.append(" , RefEnd: " + refNode.getEndPosition().getIndex());
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
