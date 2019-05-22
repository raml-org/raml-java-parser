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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.loader.ResourceLoaderExtended;
import org.raml.v2.api.loader.ResourceUriCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheResourceLoader implements ResourceLoaderExtended
{
	private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<String, byte[]> resources = new HashMap<>();
    private ResourceLoader resourceLoader;
    private File parentFile;

    public CacheResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
    
    public CacheResourceLoader(ResourceLoader resourceLoader, File parentFile)
    {
        this.resourceLoader = resourceLoader;
        this.parentFile = parentFile;
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        return fetchResource(resourceName, null);
    }

    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        try
        {
            if (resources.containsKey(resourceName))
            {
                final byte[] resourceByteArray = resources.get(resourceName);
                if (callback != null)
                {
                    if(parentFile != null) {
						File includedFile = new File(parentFile, resourceName.startsWith("/") ? resourceName.substring(1) : resourceName);
					    logger.debug("Looking for resource: {} on directory: {}...", resourceName, parentFile);
					    callback.onResourceFound(includedFile.toURI());
					}
                }
                return toInputStreamOrNull(resourceByteArray);
            }

            InputStream resource;
            if (resourceLoader instanceof ResourceLoaderExtended)
            {
                resource = ((ResourceLoaderExtended) resourceLoader).fetchResource(resourceName, callback);
            }
            else
            {
                resource = resourceLoader.fetchResource(resourceName);
            }

            // we want to cache results even if they are null
            final byte[] resourceByteArray = resource == null ? null : IOUtils.toByteArray(resource);
            resources.put(resourceName, resourceByteArray);

            return toInputStreamOrNull(resourceByteArray);

        }
        catch (final IOException e)
        {
            return resourceLoader.fetchResource(resourceName);
        }
    }

    private ByteArrayInputStream toInputStreamOrNull(final byte[] resourceByteArray)
    {
        return resourceByteArray == null ? null : new ByteArrayInputStream(resourceByteArray);
    }
}
