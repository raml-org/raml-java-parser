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
import java.util.HashSet;
import java.util.Set;

public class LibraryLinkingTransformation implements Transformer
{

    private static final ThreadLocal<Set<String>> absoluteLocations = new ThreadLocal<>();
    static {
        absoluteLocations.set(new HashSet<String>());
    }

    private ResourceLoader resourceLoader;

    public LibraryLinkingTransformation(ResourceLoader resourceLoader)
    {
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
        final String currentFile = node.getRootNode().getStartPosition().getPath();
        try
        {
            absoluteLocations.get().add(currentFile);
            try (InputStream inputStream = resourceLoader.fetchResource(absoluteLocation))
            {
                if (inputStream == null)
                {
                    return new IncludeErrorNode("Library cannot be resolved: " + absoluteLocation);
                }
                final String content = StreamUtils.toString(inputStream);
                if ( absoluteLocations.get().add(absoluteLocation) ) {
                    try {
                        absoluteLocations.get().add(absoluteLocation);
                        final Node libraryReference = new Raml10Builder()
                                .build(content, RamlFragment.Library, resourceLoader, absoluteLocation, RamlBuilder.ALL_PHASES);
                        linkNode.setLibraryReference(libraryReference);
                    } finally {
                        absoluteLocations.get().remove(absoluteLocation);
                    }
                } else {
                    return new IncludeErrorNode("Cyclic dependency in file " + currentFile + " using " + absoluteLocation);
                }
            }
        }
        catch (IOException e)
        {
            return new IncludeErrorNode(String.format("Library cannot be resolved: %s. (%s)", absoluteLocation, e.getMessage()));
        }
        finally
        {
            absoluteLocations.get().remove(currentFile);
        }

        return linkNode;
    }
}
