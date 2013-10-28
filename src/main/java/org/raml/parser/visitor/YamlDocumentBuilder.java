package org.raml.parser.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import org.raml.parser.builder.DefaultTupleBuilder;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.SequenceBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.TagResolver;
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

public class YamlDocumentBuilder<T> implements NodeHandler
{

    private Class<T> documentClass;
    private T documentObject;
    private Stack<NodeBuilder<?>> builderContext = new Stack<NodeBuilder<?>>();
    private Stack<Object> documentContext = new Stack<Object>();
    private MappingNode rootNode;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    public YamlDocumentBuilder(Class<T> documentClass, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        this.documentClass = documentClass;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
    }

    public T build(Reader content)
    {
        Yaml yamlParser = new Yaml();
        NodeVisitor nodeVisitor = new NodeVisitor(this, resourceLoader, tagResolvers);
        rootNode = (MappingNode) yamlParser.compose(content);
        preBuildProcess();
        nodeVisitor.visitDocument(rootNode);
        postBuildProcess();
        return documentObject;
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

    protected ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    protected void preBuildProcess()
    {
    }

    protected void postBuildProcess()
    {
    }

    public T build(InputStream content)
    {
        return build(new InputStreamReader(content));
    }

    public T build(String content)
    {
        return build(new StringReader(content));
    }

    public MappingNode getRootNode()
    {
        return rootNode;
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();
        Object object = ((TupleBuilder<?, MappingNode>) currentBuilder).buildValue(parentObject, mappingNode);
        documentContext.push(object);

    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode)
    {
        documentContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        SequenceBuilder currentBuilder = (SequenceBuilder) builderContext.peek();
        Object parentObject = documentContext.peek();
        switch (tupleType)
        {
            case VALUE:
                Object object = ((NodeBuilder) currentBuilder).buildValue(parentObject, node);
                builderContext.push(currentBuilder.getItemBuilder());
                documentContext.push(object);
                break;
        }
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        documentContext.pop();
        builderContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {

        NodeBuilder<?> currentBuilder = builderContext.peek();
        Object parentObject = documentContext.peek();

        switch (tupleType)
        {
            case VALUE:
                ((NodeBuilder<ScalarNode>) currentBuilder).buildValue(parentObject, node);
                break;

            default:
                ((TupleBuilder<ScalarNode, ?>) currentBuilder).buildKey(parentObject, node);

                break;
        }

    }


    @Override
    public void onDocumentStart(MappingNode node)
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
    public void onTupleStart(NodeTuple nodeTuple)
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
    public void onCustomTagStart(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
    {
    }

    @Override
    public void onCustomTagEnd(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
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
        Tag rootTag = dumperOptions.getExplicitRoot();
        Serializer serializer = new Serializer(new Emitter(output, dumperOptions), new Resolver(),
                                               dumperOptions, rootTag);
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

}
