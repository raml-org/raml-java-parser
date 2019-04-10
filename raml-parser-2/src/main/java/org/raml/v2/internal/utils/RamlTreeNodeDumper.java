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

import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.util.TreeNodeDumper;


public class RamlTreeNodeDumper extends TreeNodeDumper
{

    protected void dumpChildren(Node node)
    {
        super.dumpChildren(node);

        if (node instanceof LibraryLinkNode)
        {
            final Node refNode = ((LibraryLinkNode) node).getRefNode();
            if (refNode != null)
            {
                dump(refNode);
            }
        }
    }
}
