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
package org.raml.v2.internal.impl.v10.nodes;

import javax.annotation.Nullable;

import org.raml.v2.internal.impl.commons.nodes.OverlayableStringNode;
import org.raml.yagi.framework.grammar.rule.DefaultValue;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.KeyValueNodeImpl;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.SimpleTypeNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.yagi.framework.nodes.ObjectNodeImpl;

public class DisplayNameDefaultValue implements DefaultValue
{

    @Nullable
    @Override
    public Node getDefaultValue(Node parent)
    {
        Node grampa = parent.getParent();
        if (grampa == null)
        {
            // inside raml fragment file
            return null;
        }
        if (!(grampa instanceof KeyValueNode))
        {
            return ErrorNodeFactory.createInvalidNode(parent);
        }
        final Node keyNode = ((KeyValueNode) grampa).getKey();
        final ObjectNodeImpl result = new ObjectNodeImpl();
        if (keyNode instanceof SimpleTypeNode)
        {
            final String literalValue = ((SimpleTypeNode) keyNode).getLiteralValue();
            final OverlayableStringNode displayName = new OverlayableStringNode(literalValue);
            displayName.setSource(keyNode);
            displayName.setStartPosition(DefaultPosition.emptyPosition());
            displayName.setEndPosition(DefaultPosition.emptyPosition());
            result.addChild(new KeyValueNodeImpl(new StringNodeImpl("value"), displayName));
            return result;
        }
        else
        {
            return ErrorNodeFactory.createInvalidNode(keyNode);
        }

    }
}
