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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;

public class FileResourceLoader implements ResourceLoaderExtended
{

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private File parentPath;

    public FileResourceLoader(String path)
    {
        this(new File(path));
    }

    public FileResourceLoader(File path)
    {
        this.parentPath = path;
    }

    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        File includedFile = new File(resourceName);
        if (!includedFile.isAbsolute())
        {
            includedFile = new File(parentPath, resourceName);
            logger.debug("Looking for resource: {} on directory: {}...", resourceName, parentPath);
        }
        else
        {
            logger.debug("Looking for absolute file: {}...", resourceName);
        }
        FileInputStream inputStream = null;
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
