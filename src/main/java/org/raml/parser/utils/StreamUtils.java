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
package org.raml.parser.utils;

import static org.apache.commons.io.ByteOrderMark.UTF_8;
import static org.apache.commons.io.ByteOrderMark.UTF_16BE;
import static org.apache.commons.io.ByteOrderMark.UTF_16LE;
import static org.apache.commons.io.ByteOrderMark.UTF_32BE;
import static org.apache.commons.io.ByteOrderMark.UTF_32LE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamUtils
{

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamUtils.class);

    private static final String RAML_PARSER_ENCODING = "raml.parser.encoding";

    private static String getDefaultEncoding()
    {
        return System.getProperty(RAML_PARSER_ENCODING, "UTF-8");
    }

    public static Reader reader(InputStream stream)
    {
        try
        {
            byte[] content = IOUtils.toByteArray(stream);
            return new InputStreamReader(new ByteArrayInputStream(content), detectEncoding(content));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(stream);
        }
    }

    public static String toString(InputStream stream)
    {
        try
        {
            byte[] content = IOUtils.toByteArray(stream);
            String encoding = detectEncoding(content);
            return IOUtils.toString(new BOMInputStream(new ByteArrayInputStream(content), UTF_8, UTF_16LE, UTF_16BE, UTF_32LE, UTF_32BE), encoding);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String detectEncoding(byte[] content)
    {
        UniversalDetector detector = new UniversalDetector(null);
        detector.handleData(content, 0, content.length);
        detector.dataEnd();
        String encoding = detector.getDetectedCharset();
        if (encoding != null)
        {
            LOGGER.debug(String.format("Detected encoding: %s\n", encoding));
        }
        else
        {
            encoding = getDefaultEncoding();
            LOGGER.debug(String.format("No encoding detected, using default: %s\n", encoding));
        }
        detector.reset();
        return encoding;
    }
}
