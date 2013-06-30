package org.raml.parser.visitor;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

import org.raml.parser.builder.DefaultTupleBuilder;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.SequenceBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class YamlDocumentBuilder<T> implements NodeHandler
{

    private Class<T> documentClass;
    private T documentObject;

    private Stack<NodeBuilder<?>> builderContext = new Stack<NodeBuilder<?>>();
    private Stack<Object> documentContext = new Stack<Object>();


    public YamlDocumentBuilder(Class<T> documentClass)
    {
        this.documentClass = documentClass;

    }

    public T build(Reader content)
    {
        Yaml yamlParser = new Yaml();
        NodeVisitor nodeVisitor = new NodeVisitor(this);
        MappingNode rootNode = (MappingNode) yamlParser.compose(content);
        nodeVisitor.visitDocument(rootNode);
        return documentObject;
    }

    public T build(InputStream content)
    {
        return build(new InputStreamReader(content));
    }

    public T build(String content)
    {
        return build(new StringReader(content));
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
        DefaultTupleBuilder documentBuilder = new DefaultTupleBuilder<Node, MappingNode>(new DefaultTupleHandler());
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
            NodeBuilder<?> builder = currentBuilder.getBuiderForTuple(nodeTuple);
            builderContext.push(builder);
        }
        else
        {
            throw new IllegalStateException("Unexpected builderContext state");
        }

    }


}
