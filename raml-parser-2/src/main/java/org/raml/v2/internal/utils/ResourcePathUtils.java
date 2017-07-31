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
package org.raml.v2.internal.utils;

import java.io.File;

public class ResourcePathUtils
{

    /**
    * Returns the absolute resource location using the basePath
    * basePath and relativePath must have forward slashes(/) as path separators.
    * @param basePath The base path of the relative path
    * @param relativePath the relative path
    * @return The Absolute path
    */
    public static String toAbsoluteLocation(String basePath, String relativePath)
    {
        String result = relativePath;
        if (!isAbsolute(relativePath))
        {
            // This is for file based path
            int lastSlash = basePath.lastIndexOf("/");
            if (lastSlash == -1)
            {
                // This is for URL based path
                lastSlash = basePath.lastIndexOf("/");
            }
            if (lastSlash != -1)
            {
                result = basePath.substring(0, lastSlash + 1) + relativePath;
            }
        }
        if (result.contains("#"))
        {
            return result.split("#")[0];
        }
        return result;
    }

    public static boolean isAbsolute(String includePath)
    {
        return includePath.startsWith("http:") || includePath.startsWith("https:") || includePath.startsWith("file:") || includePath.startsWith("/") || new File(includePath).isAbsolute();
    }
}
