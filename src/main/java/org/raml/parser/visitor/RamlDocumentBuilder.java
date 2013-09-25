package org.raml.parser.visitor;

import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlDocumentBuilder extends YamlDocumentBuilder<Raml>
{

    private TemplateResolver templateResolver;

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
            getTemplateResolver().resolve(mappingNode);
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

    @Override
    protected void preBuildProcess()
    {
        getTemplateResolver().init(getRootNode());
    }

    @Override
    protected void postBuildProcess()
    {
    }
}
