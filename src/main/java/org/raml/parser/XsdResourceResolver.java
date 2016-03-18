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
package org.raml.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.LSInputImpl;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.utils.StreamUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

public class XsdResourceResolver implements LSResourceResolver
{

    private final ContextPath contextPath;
    private final ResourceLoader resourceLoader;

    public XsdResourceResolver(ContextPath contextPath, ResourceLoader resourceLoader)
    {
        this.contextPath = contextPath;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI)
    {
        if (systemId == null)
        {
            //delegate resource resolution to xml parser
            return null;
        }
        String path = contextPath.resolveAbsolutePath(systemId);
        if (path == null || path.startsWith("http://"))
        {
            //delegate resource resolution to xml parser
            return null;
        }
        InputStream inputStream = resourceLoader.fetchResource(path);
        if (inputStream == null)
        {
            //delegate resource resolution to xml parser
            return null;
        }
        byte[] content;
        try
        {
            content = IOUtils.toByteArray(inputStream);
        }
        catch (IOException e)
        {
            throw new ResolveResourceException(e);
        }
        LSInput input = new LSInputImpl(publicId, systemId, baseURI, new ByteArrayInputStream(content), StreamUtils.detectEncoding(content));
        return input;
    }
}
