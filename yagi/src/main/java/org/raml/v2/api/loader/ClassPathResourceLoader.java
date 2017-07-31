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

import static org.apache.commons.lang.StringUtils.isBlank;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.net.URISyntaxException;

import org.apache.commons.lang.StringUtils;

public class ClassPathResourceLoader implements ResourceLoaderExtended
{

    private final String rootRamlPackage;

    public ClassPathResourceLoader()
    {
        rootRamlPackage = "";
    }

    /**
     * When the root raml is loaded through the classpath allows specifying
     * the package where it is loaded from in order to correctly resolve
     * absolute path includes and libraries
     *
     * @param rootRamlPackage in the classpath
     */
    public ClassPathResourceLoader(String rootRamlPackage)
    {
        if (isBlank(rootRamlPackage) || rootRamlPackage.equals("/"))
        {
            this.rootRamlPackage = "";
        }
        else
        {

            this.rootRamlPackage = (rootRamlPackage.startsWith("/") ? rootRamlPackage.substring(1) : rootRamlPackage) + "/";
        }
    }

    @Nullable
    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        resourceName = rootRamlPackage + (resourceName.startsWith("/") ? resourceName.substring(1) : resourceName);
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null)
        {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (callback != null && inputStream != null)
        {
            try
            {
                callback.onResourceFound(Thread.currentThread().getContextClassLoader().getResource(resourceName).toURI());
            }
            catch (URISyntaxException e)
            {
                // Ignore
            }
        }

        return inputStream;
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        return fetchResource(resourceName, null);
    }
}
