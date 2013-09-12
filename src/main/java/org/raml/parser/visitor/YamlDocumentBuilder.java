package org.raml.parser.visitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Stack;

import org.raml.model.Raml;
import org.raml.parser.builder.DefaultTupleBuilder;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.SequenceBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.resolver.DefaultTupleHandler;
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
    private Stack<String> includeContext = new Stack<String>();
    private MappingNode rootNode;
    private ResourceLoader resourceLoader;

    public YamlDocumentBuilder(Class<T> documentClass)
    {
        this(documentClass, new DefaultResourceLoader());
    }

    public YamlDocumentBuilder(Class<T> documentClass, ResourceLoader resourceLoader)
    {
        this.documentClass = documentClass;
        this.resourceLoader = resourceLoader;
    }

    public T build(Reader content)
    {
        Yaml yamlParser = new Yaml();
        NodeVisitor nodeVisitor = new NodeVisitor(this, resourceLoader);
        rootNode = (MappingNode) yamlParser.compose(content);
        nodeVisitor.visitDocument(rootNode);
        postBuildProcess();
        return documentObject;
    }

    private void postBuildProcess()
    {
        if (documentObject instanceof Raml)
        {
            ((Raml) documentObject).applyTraits();
        }
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

            documentContext.push(documentClass.newInstance());
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
        documentObject = (T) documentContext.pop();
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
    public void onIncludeResourceNotFound(ScalarNode node)
    {
        throw new RuntimeException("resource not found: " + node.getValue());
    }

    @Override
    public void onIncludeStart(String includeName)
    {
        includeContext.push(includeName);
    }

    @Override
    public void onIncludeEnd(String includeName)
    {
        String include = includeContext.pop();
        if (!include.equals(includeName))
        {
            throw new IllegalStateException(String.format("include zombie! (actual: %s, expected: %s)", include, includeName));
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
