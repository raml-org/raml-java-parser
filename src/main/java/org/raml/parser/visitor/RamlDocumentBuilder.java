package org.raml.parser.visitor;

import java.lang.reflect.Field;
import java.util.Stack;

import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlDocumentBuilder extends YamlDocumentBuilder<Raml>
{

    private TemplateResolver templateResolver;
    private MediaTypeResolver mediaTypeResolver;

    public RamlDocumentBuilder()
    {
        this(new DefaultResourceLoader());
    }

    public RamlDocumentBuilder(ResourceLoader resourceLoader)
    {
        super(Raml.class, resourceLoader);
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        super.onMappingNodeStart(mappingNode);
        if (getDocumentContext().peek() instanceof Resource)
        {
            String relativeUri = ((Resource) getDocumentContext().peek()).getRelativeUri();
            getTemplateResolver().resolve(mappingNode, relativeUri);
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
