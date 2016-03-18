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
package org.raml.parser.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;
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
            return new String(trimBom(content), encoding);
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

    private static byte[] trimBom(byte[] content)
    {
        int bomSize = 0;
        if (content.length > 4)
        {
            //check for UTF_32BE and UTF_32LE BOMs
            if (content[0] == 0x00 && content[1] == 0x00 && content[2] == (byte) 0xFE && content[3] == (byte) 0xFF ||
                content[0] == (byte) 0xFF && content[1] == (byte) 0xFE && content[2] == 0x00 && content[3] == 0x00)
            {
                bomSize = 4;
            }
        }
        if (content.length > 3 && bomSize == 0)
        {
            //check for UTF-8 BOM
            if (content[0] == (byte) 0xEF && content[1] == (byte) 0xBB && content[2] == (byte) 0xBF)
            {
                bomSize = 3;
            }
        }
        if (content.length > 2 && bomSize == 0)
        {
            //check for UTF_16BE and UTF_16LE BOMs
            if (content[0] == (byte) 0xFE && content[1] == (byte) 0xFF || content[0] == (byte) 0xFF && content[1] == (byte) 0xFE)
            {
                bomSize = 2;
            }
        }

        if (bomSize > 0)
        {
            LOGGER.debug(String.format("Trimming %s-byte BOM\n", bomSize));
            int trimmedSize = content.length - bomSize;
            byte[] trimmedArray = new byte[trimmedSize];
            System.arraycopy(content, bomSize, trimmedArray, 0, trimmedSize);
            return trimmedArray;
        }
        return content;
    }

    public static String detectEncoding(byte[] content)
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
