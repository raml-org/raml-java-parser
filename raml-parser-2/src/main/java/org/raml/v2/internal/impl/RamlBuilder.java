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
package org.raml.v2.internal.impl;

import static com.google.common.collect.Iterables.limit;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import org.apache.commons.io.IOUtils;
import org.raml.v2.api.loader.CompositeResourceLoader;
import org.raml.v2.api.loader.DefaultResourceLoader;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.loader.RootRamlFileResourceLoader;
import org.raml.v2.api.loader.RootRamlUrlResourceLoader;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.v08.Raml08Builder;
import org.raml.v2.internal.impl.v10.Raml10Builder;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.Node;

/**
 * RamlBuilder create a Node representation of your raml.
 *
 * @see Node
 */
public class RamlBuilder
{

    public static int FIRST_PHASE = 1;
    public static int GRAMMAR_PHASE = 4;
    public static int LIBRARY_LINK_PHASE = 5;

    public static int ALL_PHASES = Integer.MAX_VALUE;

    private int maxPhaseNumber;

    private ResourceLoader resourceLoader = null;

    private String actualPath = null;

    public RamlBuilder()
    {
        maxPhaseNumber = ALL_PHASES;
    }

    public RamlBuilder(int maxPhaseNumber)
    {
        this.maxPhaseNumber = maxPhaseNumber;
    }

    public Node build(File ramlFile)
    {
        return build(ramlFile, new DefaultResourceLoader());
    }

    public Node build(File ramlFile, ResourceLoader resourceLoader)
    {
        this.actualPath = ramlFile.getPath();
        try (InputStream inputStream = new FileInputStream(ramlFile))
        {
            return build(StreamUtils.reader(inputStream), resourceLoader, ramlFile.getName());
        }
        catch (IOException ioe)
        {
            return ErrorNodeFactory.createInvalidInput(ioe);
        }
    }

    public Node build(String content)
    {
        return build(content, "");
    }

    public Node build(String content, String resourceLocation)
    {
        return build(content, new DefaultResourceLoader(), resourceLocation);
    }

    public Node build(String content, ResourceLoader resourceLoader, String resourceLocation)
    {
        return build(new StringReader(content), resourceLoader, resourceLocation);
    }

    public Node build(Reader content, ResourceLoader resourceLoader, String resourceLocation)
    {
        try
        {
            resourceLoader = addRootRamlResourceLoaders(resourceLoader, resourceLocation);

            final String stringContent = IOUtils.toString(content);

            // In order to be consistent between different OS, we normalize the resource location
            resourceLocation = normalizeResourceLocation(resourceLocation);

            RamlHeader ramlHeader = RamlHeader.parse(stringContent);
            Node result;
            if (RAML_10 == ramlHeader.getVersion())
            {
                result = new Raml10Builder().build(stringContent, ramlHeader.getFragment(), resourceLoader, resourceLocation, maxPhaseNumber);
            }
            else
            {
                result = new Raml08Builder().build(stringContent, resourceLoader, resourceLocation, maxPhaseNumber);
            }
            return result;
        }
        catch (IOException ioe)
        {
            return ErrorNodeFactory.createInvalidInput(ioe);
        }
        catch (RamlHeader.InvalidHeaderVersionException e)
        {
            return ErrorNodeFactory.createUnsupportedVersion(e.getMessage());
        }
        catch (RamlHeader.InvalidHeaderFragmentException e)
        {
            return ErrorNodeFactory.createInvalidFragmentName(e.getMessage());
        }
        catch (RamlHeader.MissingHeaderException e)
        {
            return ErrorNodeFactory.createEmptyDocument();
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            return ErrorNodeFactory.createInvalidHeader(e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(content);
        }
    }

    private ResourceLoader addRootRamlResourceLoaders(ResourceLoader resourceLoader, String resourceLocation)
    {
        resourceLoader = addRootRamlFileResourceLoader(resourceLoader, resourceLocation);
        resourceLoader = addRootRamlUrlResourceLoader(resourceLoader, resourceLocation);
        this.resourceLoader = resourceLoader;
        return resourceLoader;
    }

    private ResourceLoader addRootRamlFileResourceLoader(ResourceLoader resourceLoader, String resourceLocation)
    {
        File parentFile = new File(actualPath != null ? actualPath : resourceLocation).getParentFile();
        if (parentFile != null)
        {
            resourceLoader = new CompositeResourceLoader(new RootRamlFileResourceLoader(parentFile), resourceLoader);
        }

        return resourceLoader;
    }

    private ResourceLoader addRootRamlUrlResourceLoader(ResourceLoader resourceLoader, String resourceLocation)
    {
        String rootRamlPath = getRootPath(resourceLocation);
        if (!Strings.isNullOrEmpty(rootRamlPath))
        {
            resourceLoader = new CompositeResourceLoader(new RootRamlUrlResourceLoader(rootRamlPath), resourceLoader);
        }

        return resourceLoader;
    }

    private String getRootPath(String rootRamlFileUrl)
    {
        final List<String> urlSegments = Splitter.on("/").splitToList(rootRamlFileUrl);
        if (urlSegments.isEmpty())
        {
            return "";
        }
        return Joiner.on("/").join(limit(urlSegments, urlSegments.size() - 1));
    }

    private String normalizeResourceLocation(String resourceLocation)
    {
        return resourceLocation.replace("\\", "/");
    }

    public ResourceLoader getResourceLoader()
    {
        return this.resourceLoader;
    }

    public String getActualPath()
    {
        return actualPath;
    }


}
