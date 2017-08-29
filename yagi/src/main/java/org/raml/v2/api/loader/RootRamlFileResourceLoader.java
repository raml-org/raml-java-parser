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
package org.raml.v2.api.loader;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RootRamlFileResourceLoader implements ResourceLoaderExtended
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private File parentPath;

    public RootRamlFileResourceLoader(File path)
    {
        this.parentPath = path;
    }

    @Nullable
    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        FileInputStream inputStream = null;
        File includedFile = new File(parentPath, resourceName.startsWith("/") ? resourceName.substring(1) : resourceName);
        logger.debug("Looking for resource: {} on directory: {}...", resourceName, parentPath);
        try
        {
            inputStream = new FileInputStream(includedFile);

            if (callback != null)
            {
                callback.onResourceFound(includedFile.toURI());
            }
        }
        catch (FileNotFoundException e)
        {
            // ignore
        }

        return inputStream;
    }

    @Nullable
    @Override
    public InputStream fetchResource(String resourceName)
    {
        return fetchResource(resourceName, null);
    }
}
