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
package org.raml.v2.internal.impl.commons.phase;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.raml.v2.internal.framework.nodes.ObjectNode;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.IncludeErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.framework.nodes.snakeyaml.RamlNodeParser;
import org.raml.v2.internal.framework.nodes.snakeyaml.SYIncludeNode;
import org.raml.v2.internal.framework.phase.Transformer;
import org.raml.v2.internal.impl.commons.nodes.RamlTypedFragmentNode;
import org.raml.v2.internal.impl.v10.RamlFragment;
import org.raml.v2.internal.utils.StreamUtils;


public class IncludeResolver implements Transformer
{

    private final ResourceLoader resourceLoader;
    private final String resourceLocation;

    public IncludeResolver(ResourceLoader resourceLoader, String resourceLocation)
    {
        this.resourceLoader = resourceLoader;
        this.resourceLocation = resourceLocation;
    }

    @Override
    public boolean matches(Node tree)
    {
        return tree instanceof SYIncludeNode;
    }

    @Override
    public Node transform(Node tree)
    {

        SYIncludeNode includeNode = (SYIncludeNode) tree;
        String resourcePath = applyResourceLocation(includeNode.getIncludePath());
        try (InputStream inputStream = resourceLoader.fetchResource(resourcePath))
        {
            if (inputStream == null)
            {
                String msg = "Include cannot be resolved: " + resourcePath;
                return new IncludeErrorNode(msg);
            }
            Node result;
            String includeContent = StreamUtils.toString(inputStream);
            if (resourcePath.endsWith(".raml") || resourcePath.endsWith(".yaml") || resourcePath.endsWith(".yml"))
            {

                try
                {
                    RamlHeader ramlHeader = RamlHeader.parse(includeContent);
                    final RamlFragment fragment = ramlHeader.getFragment();
                    result = RamlNodeParser.parse(resourcePath, includeContent, fragment != null);
                    if (result != null && isTypedFragment(result, fragment))
                    {
                        final RamlTypedFragmentNode newNode = new RamlTypedFragmentNode(fragment);
                        result.replaceWith(newNode);
                        result = newNode;
                    }
                }
                catch (RamlHeader.InvalidHeaderException e)
                {
                    // no valid header defined => !supportUses
                    result = RamlNodeParser.parse(resourcePath, includeContent, false);
                }

            }
            else
            // scalar value
            {
                result = new StringNodeImpl(includeContent);
            }

            if (result == null)
            {
                String msg = "Include file is empty: " + resourcePath;
                result = new IncludeErrorNode(msg);
            }

            return result;
        }
        catch (IOException e)
        {
            String msg = String.format("Include cannot be resolved: %s. (%s)", resourcePath, e.getMessage());
            return new IncludeErrorNode(msg);
        }
    }

    private boolean isTypedFragment(Node result, RamlFragment fragment)
    {
        return fragment != null && fragment != RamlFragment.Library && result instanceof ObjectNode;
    }

    private String applyResourceLocation(String includePath)
    {
        String result = includePath;
        if (!isAbsolute(includePath))
        {
            int lastSlash = resourceLocation.lastIndexOf("/");
            if (lastSlash != -1)
            {
                result = resourceLocation.substring(0, lastSlash + 1) + includePath;
            }
        }
        if (result.contains("#"))
        {
            return result.split("#")[0];
        }
        return result;
    }

    private boolean isAbsolute(String includePath)
    {
        return includePath.startsWith("http:") || includePath.startsWith("https:") || includePath.startsWith("file:") || new File(includePath).isAbsolute();
    }
}
