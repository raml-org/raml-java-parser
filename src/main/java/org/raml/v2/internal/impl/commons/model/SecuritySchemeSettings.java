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
package org.raml.v2.internal.impl.commons.model;

import java.util.List;

import org.raml.v2.internal.framework.nodes.KeyValueNode;
import org.raml.v2.internal.framework.nodes.Node;

public class SecuritySchemeSettings extends Annotable
{

    private KeyValueNode node;

    public SecuritySchemeSettings(Node node)
    {
        this.node = (KeyValueNode) node;
    }

    @Override
    protected Node getNode()
    {
        return node.getValue();
    }

    public StringType requestTokenUri()
    {
        return getStringTypeValue("requestTokenUri");
    }

    public StringType authorizationUri()
    {
        return getStringTypeValue("authorizationUri");
    }

    public StringType tokenCredentialsUri()
    {
        return getStringTypeValue("tokenCredentialsUri");
    }

    public List<String> signatures()
    {
        return getStringList("signatures");
    }

    public StringType accessTokenUri()
    {
        return getStringTypeValue("accessTokenUri");
    }

    public List<String> authorizationGrants()
    {
        return getStringList("authorizationGrants");
    }

    public List<String> scopes()
    {
        return getStringList("scopes");
    }
}
