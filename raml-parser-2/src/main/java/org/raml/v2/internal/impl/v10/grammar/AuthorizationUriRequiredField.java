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
package org.raml.v2.internal.impl.v10.grammar;

import org.raml.yagi.framework.grammar.rule.RequiredField;
import org.raml.yagi.framework.nodes.ArrayNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.util.NodeSelector;


public class AuthorizationUriRequiredField implements RequiredField
{

    @Override
    public boolean isRequiredField(Node parent)
    {
        ArrayNode grants = (ArrayNode) NodeSelector.selectFrom("authorizationGrants", parent);
        if (grants != null)
        {
            for (Node grantNode : grants.getChildren())
            {
                String grant = ((StringNode) grantNode).getValue();
                if ("implicit".equals(grant) || "authorization_code".equals(grant))
                {
                    return true;
                }
            }
        }
        return false;
    }
}
