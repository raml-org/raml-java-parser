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
package org.raml.v2.internal.impl.v10.phase;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.v10.Raml10Builder;
import org.raml.v2.internal.impl.v10.nodes.LibraryLinkNode;
import org.raml.v2.internal.utils.ResourcePathUtils;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.yagi.framework.nodes.IncludeErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.Transformer;

import java.io.IOException;
import java.io.InputStream;

import static org.raml.v2.api.model.v10.RamlFragment.Library;
import static org.raml.v2.internal.impl.RamlBuilder.ALL_PHASES;

public class LibraryLinkingTransformation implements Transformer
{

    private final Raml10Builder builder;
    private final ResourceLoader resourceLoader;

    public LibraryLinkingTransformation(Raml10Builder builder, ResourceLoader resourceLoader)
    {
        this.builder = builder;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof LibraryLinkNode;
    }

    @Override
    public Node transform(Node node)
    {
        final LibraryLinkNode linkNode = (LibraryLinkNode) node;
        final String baseLocation = linkNode.getStartPosition().getPath();
        final String refName = linkNode.getRefName();
        final String absoluteLocation = ResourcePathUtils.toAbsoluteLocation(baseLocation, refName);
        try
        {
            try (InputStream inputStream = resourceLoader.fetchResource(absoluteLocation))
            {
                if (inputStream == null)
                {
                    return new IncludeErrorNode("Library cannot be resolved: " + absoluteLocation);
                }
                final String content = StreamUtils.toString(inputStream);
                final Node libraryReference = builder
                                                     .build(linkNode, content, Library, resourceLoader, absoluteLocation, ALL_PHASES);
                linkNode.setLibraryReference(libraryReference);
            }
        }
        catch (IOException e)
        {
            return new IncludeErrorNode(String.format("Library cannot be resolved: %s. (%s)", absoluteLocation, e.getMessage()));
        }

        return linkNode;
    }
}
