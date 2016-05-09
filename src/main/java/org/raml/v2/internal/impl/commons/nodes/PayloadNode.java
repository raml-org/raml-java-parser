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

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;

public class PayloadNode extends ExampleTypeNode
{

    private Node type;

    private boolean isArray = false;


    public PayloadNode(Node type, String value)
    {
        this.type = type;
        this.setSource(new StringNodeImpl(value));
    }

    public PayloadNode(Node type, String value, boolean isArray)
    {
        this(type, value);
        this.isArray = isArray;
    }


    public boolean isArrayExample()
    {
        return isArray;
    }

    public Node getTypeNode()
    {
        return type;
    }
}
