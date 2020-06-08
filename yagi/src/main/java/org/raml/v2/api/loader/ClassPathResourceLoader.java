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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ClassPathResourceLoader implements ResourceLoaderExtended
{

    private final String rootRamlPackage;
    private URI callbackParam;

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

    private URL getResource(String uselessRootPackage, String resourceName)
    {
        String fixedResourceName = uselessRootPackage + (resourceName.startsWith("/") ? resourceName.substring(1) : resourceName);
        URL url = getClass().getClassLoader().getResource(fixedResourceName);
        if (url == null)
        {
            return Thread.currentThread().getContextClassLoader().getResource(fixedResourceName);
        }

        return url;
    }

    @Nullable
    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {

        try
        {
            final URL url = getResource(rootRamlPackage, resourceName);
            if (url != null)
            {

                this.callbackParam = url.toURI();
                if (callback != null)
                {
                    callback.onResourceFound(callbackParam);
                }

                return url.openStream();
            }
            return null;
        }
        catch (IOException | URISyntaxException e)
        {

            return null;
        }
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        return fetchResource(resourceName, null);
    }

    @Override
    public URI getUriCallBackParam()
    {
        return callbackParam;
    }
}
