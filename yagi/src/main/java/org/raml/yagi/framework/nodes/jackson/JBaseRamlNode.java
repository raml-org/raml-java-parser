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
package org.raml.yagi.framework.nodes.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.NumericNode;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.BaseNode;
import org.raml.yagi.framework.nodes.DefaultPosition;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class JBaseRamlNode extends BaseNode
{

    private JsonNode jsonNode;
    private String resourcePath;
    private final ResourceLoader resourceLoader;

    public JBaseRamlNode(JsonNode jsonNode, String resourcePath, ResourceLoader resourceLoader)
    {
        this.jsonNode = jsonNode;
        this.resourcePath = resourcePath;
        this.resourceLoader = resourceLoader;
    }

    protected JBaseRamlNode(JBaseRamlNode node)
    {
        super(node);
        this.jsonNode = node.getJsonNode();
        this.resourcePath = node.getResourcePath();
        this.resourceLoader = node.getResourceLoader();
    }

    public JsonNode getJsonNode()
    {
        return jsonNode;
    }

    public String getResourcePath()
    {
        return resourcePath;
    }

    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    @Nullable
    public String getLiteralValue()
    {
        if (jsonNode instanceof NumericNode)
        {
            return (getJsonNode()).textValue();
        }
        return getJsonNode().textValue();
    }

    @Nonnull
    @Override
    public Position getStartPosition()
    {
        return DefaultPosition.emptyPosition();
    }

    @Nonnull
    @Override
    public Position getEndPosition()
    {
        return DefaultPosition.emptyPosition();
    }

}
