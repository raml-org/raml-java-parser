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
package org.raml.yagi.framework.nodes.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

public class JsonUtils
{
    private static final String JSON_STRICT_DUPLICATE_DETECTION_PROPERTY = "yagi.json_duplicate_keys_detection";
    private static final boolean STRICT_DUPLICATE_DETECTION_VALUE =
            Boolean.valueOf(System.getProperty(JSON_STRICT_DUPLICATE_DETECTION_PROPERTY, "true"));


    public static JsonNode parseJson(String value) throws IOException
    {
        return parseJson(new StringReader(value));
    }

    public static JsonNode parseJson(Reader reader) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disableDefaultTyping();
        mapper.configure(JsonParser.Feature.STRICT_DUPLICATE_DETECTION, STRICT_DUPLICATE_DETECTION_VALUE);
        return mapper.readValue(reader, JsonNode.class);
    }

}
