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

import javax.annotation.Nullable;
import java.io.InputStream;

public class CompositeResourceLoader implements ResourceLoaderExtended
{

    private ResourceLoader[] resourceLoaders;

    public CompositeResourceLoader(ResourceLoader... resourceLoaders)
    {
        this.resourceLoaders = resourceLoaders;
    }

    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        InputStream inputStream = null;
        for (ResourceLoader loader : resourceLoaders)
        {
            if (loader instanceof ResourceLoaderExtended)
            {
                inputStream = ((ResourceLoaderExtended) loader).fetchResource(resourceName, callback);
            }
            else
            {
                inputStream = loader.fetchResource(resourceName);
            }

            if (inputStream != null)
            {
                break;
            }
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
