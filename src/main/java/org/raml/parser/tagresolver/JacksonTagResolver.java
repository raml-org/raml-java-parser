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
package org.raml.parser.tagresolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;

import java.io.ByteArrayOutputStream;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;


public class JacksonTagResolver implements TagResolver
{

    public static final Tag JACKSON_TAG = new Tag("!jackson");

    @Override
    public boolean handles(Tag tag)
    {
        return JACKSON_TAG.equals(tag);
    }

    @Override
    public Node resolve(Node node, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        String className = ((ScalarNode) node).getValue();
        try
        {
            Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
            ObjectMapper objectMapper = new ObjectMapper();
            SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
            objectMapper.acceptJsonFormatVisitor(objectMapper.constructType(clazz), visitor);
            JsonSchema jsonSchema = visitor.finalSchema();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            objectMapper.writeValue(baos, jsonSchema);
            String schema = baos.toString();
            return new ScalarNode(Tag.STR, schema, node.getStartMark(), node.getEndMark(), ((ScalarNode) node).getStyle());
        }
        catch (Exception e)
        {
            throw new YAMLException(e);
        }
    }

    @Override
    public void beforeProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
    {
        //do nothing
    }

    @Override
    public void afterProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
    {
        //do nothing
    }
}
