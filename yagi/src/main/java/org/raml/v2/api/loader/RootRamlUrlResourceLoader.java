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
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;

public class RootRamlUrlResourceLoader implements ResourceLoaderExtended
{
    public static final String APPLICATION_RAML = "application/raml+yaml";

    private String rootRamlUrl;

    public RootRamlUrlResourceLoader(String rootRamlUrl)
    {
        this.rootRamlUrl = rootRamlUrl.endsWith("/") ? rootRamlUrl : rootRamlUrl + "/";
    }

    @Override
    public InputStream fetchResource(String resourceName, ResourceUriCallback callback)
    {
        InputStream inputStream = null;
        try
        {
            URL url = new URL(resourceName.startsWith(rootRamlUrl) ? resourceName : rootRamlUrl + resourceName);
            URLConnection connection = url.openConnection();
            connection.setRequestProperty("Accept", APPLICATION_RAML + ", */*");
            inputStream = new BufferedInputStream(connection.getInputStream());

            if (callback != null)
            {
                callback.onResourceFound(url.toURI());
            }
        }
        catch (IOException e)
        {
            // ignore on resource not found
        }
        catch (URISyntaxException e)
        {
            // Ignore
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
