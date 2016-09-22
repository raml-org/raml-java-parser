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
package org.raml.yagi.framework.nodes.snakeyaml;

import javax.annotation.Nonnull;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.nodes.AbstractPosition;
import org.yaml.snakeyaml.error.Mark;

public class SYPosition extends AbstractPosition
{

    private Mark mark;
    private ResourceLoader resourceLoader;
    private String resourcePath;
    private String includedResourceURI;

    public SYPosition(Mark mark, ResourceLoader resourceLoader, String resourcePath)
    {
        this.mark = mark;
        this.resourceLoader = resourceLoader;
        this.resourcePath = resourcePath;
    }

    @Override
    public int getIndex()
    {
        return mark.getIndex();
    }

    @Override
    public int getLine()
    {
        return mark.getLine();
    }

    @Override
    public int getColumn()
    {
        return mark.getColumn();
    }

    @Nonnull
    @Override
    public String getPath()
    {
        return resourcePath;
    }

    @Override
    public String getIncludedResourceUri()
    {
        return includedResourceURI;
    }

    @Override
    public void setIncludedResourceUri(String includedResourceURI)
    {
        this.includedResourceURI = includedResourceURI;
    }

    @Nonnull
    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }
}
