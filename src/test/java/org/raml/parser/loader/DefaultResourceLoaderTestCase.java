/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.loader;

import static junit.framework.Assert.assertNotNull;

import java.io.File;

import org.junit.Test;
import org.raml.parser.loader.DefaultResourceLoader;

public class DefaultResourceLoaderTestCase
{

    private static final String RESOURCE_PATH = "src/test/resources/org/raml/full-config.yaml";

    @Test
    public void relativeFilePath()
    {
        assertNotNull(new DefaultResourceLoader().fetchResource(RESOURCE_PATH));
    }

    @Test
    public void absoluteFilePath()
    {
        String path = new File(new File("").getAbsoluteFile(), RESOURCE_PATH).getAbsolutePath();  
        assertNotNull(new DefaultResourceLoader().fetchResource(path));
    }
}
