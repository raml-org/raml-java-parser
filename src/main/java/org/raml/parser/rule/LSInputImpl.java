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
package org.raml.parser.rule;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;

import org.w3c.dom.ls.LSInput;

public class LSInputImpl implements LSInput
{

    protected Reader characterStream;
    protected InputStream byteStream;
    protected String stringData;

    protected String systemId;
    protected String publicId;
    protected String baseUri;

    protected String encoding;
    protected boolean certifiedText;

    public LSInputImpl(String publicId, String systemId, String baseURI, ByteArrayInputStream content, String encoding)
    {
        this.publicId = publicId;
        this.systemId = systemId;
        this.baseUri = baseURI;
        this.byteStream = content;
        this.encoding = encoding;
    }

    @Override
    public Reader getCharacterStream()
    {
        return characterStream;
    }

    @Override
    public void setCharacterStream(Reader characterStream)
    {
        this.characterStream = characterStream;
    }

    @Override
    public InputStream getByteStream()
    {
        return byteStream;
    }

    @Override
    public void setByteStream(InputStream byteStream)
    {
        this.byteStream = byteStream;
    }

    @Override
    public String getStringData()
    {
        return stringData;
    }

    @Override
    public void setStringData(String stringData)
    {
        this.stringData = stringData;
    }

    @Override
    public String getSystemId()
    {
        return systemId;
    }

    @Override
    public void setSystemId(String systemId)
    {
        this.systemId = systemId;
    }

    @Override
    public String getPublicId()
    {
        return publicId;
    }

    @Override
    public void setPublicId(String publicId)
    {
        this.publicId = publicId;
    }

    @Override
    public String getBaseURI()
    {
        return baseUri;
    }

    @Override
    public void setBaseURI(String baseURI)
    {
        this.baseUri = baseURI;
    }

    @Override
    public String getEncoding()
    {
        return encoding;
    }

    @Override
    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    @Override
    public boolean getCertifiedText()
    {
        return certifiedText;
    }

    @Override
    public void setCertifiedText(boolean certifiedText)
    {
        this.certifiedText = certifiedText;
    }
}
