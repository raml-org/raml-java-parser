package org.raml.parser.visitor;

import org.raml.model.Raml;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.NodeRuleFactoryExtension;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlValidationService extends YamlValidationService
{

    private TemplateResolver templateResolver;

    public RamlValidationService(YamlValidator... yamlValidators)
    {
        super(yamlValidators);
    }

    public RamlValidationService(ResourceLoader resourceLoader, YamlValidator... yamlValidators)
    {
        super(resourceLoader, yamlValidators);
    }

    public static RamlValidationService createDefault()
    {
        return new RamlValidationService(new YamlDocumentValidator(Raml.class));
    }

    public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(getResourceLoader(), getNodeHandler());
        }
        return templateResolver;
    }

    @Override
    protected void preValidation(MappingNode root)
    {
        getTemplateResolver().init(root);
    }

    public static RamlValidationService createDefault(ResourceLoader loader)
    {
        return new RamlValidationService(loader, new YamlDocumentValidator(Raml.class));
    }

    public static RamlValidationService createDefault(Class<?> clazz, ResourceLoader loader, NodeRuleFactoryExtension... extensions)
    {
        return new RamlValidationService(loader, new YamlDocumentValidator(clazz, new NodeRuleFactory(extensions)));
    }
}
