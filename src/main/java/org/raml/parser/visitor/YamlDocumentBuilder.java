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
package org.raml.parser.visitor;

import static org.raml.parser.rule.ValidationMessage.NON_SCALAR_KEY_MESSAGE;
import static org.raml.parser.visitor.TupleType.KEY;
import static org.raml.parser.visitor.TupleType.VALUE;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import org.apache.commons.io.IOUtils;
import org.raml.parser.builder.DefaultTupleBuilder;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.SequenceBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.loader.ResourceNotFoundException;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.tagresolver.ContextPathAware;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.utils.StreamUtils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.serializer.Serializer;

public class YamlDocumentBuilder<T> implements NodeHandler, ContextPathAware
{

    private Class<T> documentClass;
    private T documentObject;
    private Stack<NodeBuilder<?>> builderContext = new Stack<NodeBuilder<?>>();
    private Stack<Object> documentContext = new Stack<Object>();
    private MappingNode rootNode;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;
    private ContextPath contextPath;

    public YamlDocumentBuilder(Class<T> documentClass, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        this.documentClass = documentClass;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
    }

    public T build(String resourceLocation)
    {
        InputStream resourceStream = resourceLoader.fetchResource(resourceLocation);
        return build(resourceStream, resourceLocation);
    }

    public T build(Reader content, String resourceLocation)
    {
        if (content == null)
        {
            throw new ResourceNotFoundException(resourceLocation);
        }
        try
        {
            Yaml yamlParser = new Yaml();
            NodeVisitor nodeVisitor = new NodeVisitor(this, resourceLoader, tagResolvers);
            rootNode = (MappingNode) yamlParser.compose(content);
            contextPath.pushRoot(resourceLocation);
            preBuildProcess();
            nodeVisitor.visitDocument(rootNode);
            postBuildProcess();
            return documentObject;
        }
        finally
        {
            IOUtils.closeQuietly(content);
        }
    }

    public T build(InputStream content, String resourceLocation)
    {
        if (content == null)
        {
            throw new ResourceNotFoundException(resourceLocation);
        }
        return build(StreamUtils.reader(content), resourceLocation);
    }

    public T build(String content, String resourceLocation)
    {
        return build(new StringReader(content), resourceLocation);
    }

    @Deprecated
    public T build(Reader content)
    {
        return build(content, new File("").getPath());
    }

    @Deprecated
    public T build(InputStream content)
    {
        return build(StreamUtils.reader(content));
    }

    protected T getDocumentObject()
    {
        return documentObject;
    }

    protected Stack<NodeBuilder<?>> getBuilderContext()
    {
        return builderContext;
    }

    protected Stack<Object> getDocumentContext()
    {
        return documentContext;
    }

    public ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    protected void preBuildProcess()
    {
    }

    protected void postBuildProcess()
    {
    }

    public MappingNode getRootNode()
    {
        return rootNode;
    }

    @Override
    public boolean onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + mappingNode.getStartMark());
        }
        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();
        Object object = ((TupleBuilder<?, MappingNode>) currentBuilder).buildValue(parentObject, mappingNode);
        documentContext.push(object);
        return true;

    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + mappingNode.getStartMark());
        }
        documentContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + node.getStartMark());
        }
        SequenceBuilder currentBuilder = (SequenceBuilder) builderContext.peek();
        Object parentObject = documentContext.peek();
        Object object = ((NodeBuilder) currentBuilder).buildValue(parentObject, node);
        builderContext.push(currentBuilder.getItemBuilder());
        documentContext.push(object);
        return true;
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        if (tupleType == KEY)
        {
            throw new YAMLException(NON_SCALAR_KEY_MESSAGE + ": " + node.getStartMark());
        }
        documentContext.pop();
        builderContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {

        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();

        if (tupleType == VALUE)
        {
            ((NodeBuilder<ScalarNode>) currentBuilder).buildValue(parentObject, node);
        }
        else
        {
            ((TupleBuilder<ScalarNode, ?>) currentBuilder).buildKey(parentObject, node);
        }

    }


    @Override
    public boolean onDocumentStart(MappingNode node)
    {
        try
        {
            documentObject = documentClass.newInstance();
            documentContext.push(documentObject);
            builderContext.push(buildDocumentBuilder());
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        return true;
    }

    private TupleBuilder<?, ?> buildDocumentBuilder()
    {
        DefaultTupleBuilder<Node, MappingNode> documentBuilder = new DefaultTupleBuilder<Node, MappingNode>(new DefaultTupleHandler());
        documentBuilder.addBuildersFor(documentClass);
        return documentBuilder;
    }


    @Override
    public void onDocumentEnd(MappingNode node)
    {
        if (documentObject != documentContext.pop())
        {
            throw new IllegalStateException("more zombies?!");
        }
    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        builderContext.pop();
    }

    @Override
    public boolean onTupleStart(NodeTuple nodeTuple)
    {
        TupleBuilder<?, ?> currentBuilder = (TupleBuilder<?, ?>) builderContext.peek();
        if (currentBuilder != null)
        {
            NodeBuilder<?> builder = currentBuilder.getBuilderForTuple(nodeTuple);
            builderContext.push(builder);
        }
        else
        {
            throw new IllegalStateException("Unexpected builderContext state");
        }
        return true;
    }

    @Override
    public void onSequenceElementStart(Node sequenceNode)
    {
    }

    @Override
    public void onSequenceElementEnd(Node sequenceNode)
    {
    }

    @Override
    public void onCustomTagStart(Tag tag, Node originalValueNode, Node node)
    {
    }

    @Override
    public void onCustomTagEnd(Tag tag, Node originalValueNode, Node node)
    {
    }

    @Override
    public void onCustomTagError(Tag tag, Node node, String message)
    {
        if (IncludeResolver.INCLUDE_TAG.equals(tag))
        {
            throw new RuntimeException("resource not found: " + ((ScalarNode) node).getValue());
        }
    }

    public static String dumpFromAst(Node rootNode)
    {
        Writer writer = new StringWriter();
        dumpFromAst(rootNode, writer);
        return writer.toString();
    }

    public static void dumpFromAst(Node rootNode, Writer output)
    {
        if (rootNode == null)
        {
            throw new IllegalArgumentException("rootNode is null");
        }
        DumperOptions dumperOptions = new DumperOptions();
        Serializer serializer = new Serializer(new Emitter(output, dumperOptions), new Resolver(),
                                               dumperOptions, null);
        try
        {
            serializer.open();
            serializer.serialize(rootNode);
            serializer.close();
        }
        catch (IOException e)
        {
            throw new YAMLException(e);
        }
    }

    @Override
    public void setContextPath(ContextPath contextPath)
    {
        this.contextPath = contextPath;
    }

    @Override
    public ContextPath getContextPath()
    {
        return contextPath;
    }
}
