package heaven.parser.visitor;

import java.io.StringReader;
import java.util.Stack;

import heaven.parser.builder.DefaultTupleBuilder;
import heaven.parser.builder.NodeBuilder;
import heaven.parser.builder.SequenceBuilder;
import heaven.parser.builder.TupleBuilder;
import heaven.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;

public class BuilderNodeHandler<T> implements NodeHandler
{

    private Class<T> documentClass;
    private T documentObject;

    private Stack<NodeBuilder<?>> builderContext = new Stack<NodeBuilder<?>>();
    private Stack<Object> objectContext = new Stack<Object>();


    public BuilderNodeHandler(Class<T> documentClass)
    {
        this.documentClass = documentClass;

    }

    public T build(String content)
    {
        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(this);
            for (Node data : yamlParser.composeAll(new StringReader(content)))
            {
                if (data instanceof MappingNode)
                {
                    nodeVisitor.visitDocument((MappingNode) data);
                }

            }

        }
        catch (YAMLException ex)
        {
            throw new RuntimeException(ex);
        }
        return documentObject;
    }


    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        NodeBuilder<?> peek = builderContext.peek();
        Object parentObject = objectContext.peek();
        Object object = ((TupleBuilder<?, MappingNode>) peek).buildValue(parentObject, mappingNode);
        objectContext.push(object);

    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode)
    {
        objectContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        SequenceBuilder peek = (SequenceBuilder) builderContext.peek();
        Object parentObject = objectContext.peek();
        switch (tupleType)
        {
            case VALUE:
                Object object = ((NodeBuilder) peek).buildValue(parentObject, node);
                builderContext.push(peek.getItemBuilder());
                objectContext.push(object);
                break;
        }
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {
        objectContext.pop();
        builderContext.pop();
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {

        NodeBuilder<?> peek = builderContext.peek();
        Object parentObject = objectContext.peek();

        switch (tupleType)
        {
            case VALUE:
                ((NodeBuilder<ScalarNode>) peek).buildValue(parentObject, node);
                break;

            default:
                ((TupleBuilder<ScalarNode, ?>) peek).buildKey(parentObject, node);

                break;
        }

    }


    @Override
    public void onDocumentStart(MappingNode node)
    {
        try
        {

            objectContext.push(documentClass.newInstance());
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
        documentObject = (T) objectContext.pop();
    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        builderContext.pop();

    }

    @Override
    public void onTupleStart(NodeTuple nodeTuple)
    {

        TupleBuilder<?, ?> tupleBuilder = (TupleBuilder<?, ?>) builderContext.peek();
        if (tupleBuilder != null)
        {
            NodeBuilder<?> builder = tupleBuilder.getBuiderForTuple(nodeTuple);
            builderContext.push(builder);
        }
        else
        {
            throw new IllegalStateException("Unexpected builderContext state");
        }

    }


}
