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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.raml.yagi.framework.nodes.jackson.JsonUtils.parseJson;

public class JNodeParser
{

    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, Reader reader)
    {
        try
        {
            JsonNode rootNode = parseJson(reader);
            return new JModelWrapper(resourceLoader, resourcePath).wrap(rootNode);
        }
        catch (JsonMappingException | JsonParseException e)
        {
            return ErrorNodeFactory.createInvalidJsonExampleNode(e.getOriginalMessage());
        }
        catch (IOException e)
        {
            return new ErrorNode(e.getMessage());
        }

    }


    @Nullable
    public static Node parse(ResourceLoader resourceLoader, String resourcePath, String content)
    {
        return parse(resourceLoader, resourcePath, new StringReader(content));
    }

}
