/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.visitor;

import java.lang.reflect.Field;
import java.util.Stack;

import org.apache.commons.lang.ArrayUtils;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlDocumentBuilder extends YamlDocumentBuilder<Raml>
{

    private TemplateResolver templateResolver;
    private MediaTypeResolver mediaTypeResolver;

    public RamlDocumentBuilder()
    {
        this(new DefaultResourceLoader());
    }

    public RamlDocumentBuilder(ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        super(Raml.class, resourceLoader, defaultResolver(tagResolvers));
    }

    private static TagResolver[] defaultResolver(TagResolver[] tagResolvers)
    {
        TagResolver[] defaultResolvers = new TagResolver[] {
                new IncludeResolver(),
                new JacksonTagResolver(),
                new JaxbTagResolver()
        };
        return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        super.onMappingNodeStart(mappingNode);
        if (getDocumentContext().peek() instanceof Resource)
        {
            Resource resource = (Resource) getDocumentContext().peek();
            getTemplateResolver().resolve(mappingNode, resource.getRelativeUri(), resource.getUri());
        }
        else if (isBodyBuilder(getBuilderContext().peek()))
        {
            getMediaTypeResolver().resolve(mappingNode);
        }
    }

    private String toString(Stack<NodeBuilder<?>> builderContext)
    {
        StringBuilder builder = new StringBuilder(">>> BuilderContext >>> ");
        for (NodeBuilder nb : builderContext)
        {
            builder.append(nb).append(" ->- ");
        }
        return builder.toString();
    }

    private boolean isBodyBuilder(NodeBuilder builder)
    {
        try
        {
            Field valueType = builder.getClass().getDeclaredField("valueClass");
            valueType.setAccessible(true);
            return valueType.get(builder) != null && ((Class) valueType.get(builder)).getName().equals("org.raml.model.MimeType");
        }
        catch (NoSuchFieldException e)
        {
            return false;
        }
        catch (IllegalAccessException e)
        {
            return false;
        }
    }

    public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(getResourceLoader(), this);
        }
        return templateResolver;
    }

    public MediaTypeResolver getMediaTypeResolver()
    {
        if (mediaTypeResolver == null)
        {
            mediaTypeResolver = new MediaTypeResolver();
        }
        return mediaTypeResolver;
    }

    @Override
    protected void preBuildProcess()
    {
        getTemplateResolver().init(getRootNode());
        getMediaTypeResolver().beforeDocumentStart(getRootNode());
    }

    @Override
    protected void postBuildProcess()
    {
    }
}
