package org.raml.parser.visitor;

import java.util.List;

import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.NodeRuleFactoryExtension;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlValidationService extends YamlValidationService
{

    private RamlDocumentValidator validator;

    public RamlValidationService(RamlDocumentValidator ramlDocumentValidator)
    {
        this(new DefaultResourceLoader(), ramlDocumentValidator);
    }

    public RamlValidationService(ResourceLoader resourceLoader, RamlDocumentValidator ramlDocumentValidator)
    {
        super(resourceLoader, ramlDocumentValidator);
        validator = ramlDocumentValidator;
        validator.setResourceLoader(resourceLoader);
    }

    public TemplateResolver getTemplateResolver()
    {
        return validator.getTemplateResolver();
    }

    @Override
    protected List<ValidationResult> preValidation(MappingNode root)
    {
        return getTemplateResolver().init(root);
    }

    public static RamlValidationService createDefault()
    {
        return new RamlValidationService(new RamlDocumentValidator());
    }

    public static RamlValidationService createDefault(ResourceLoader loader)
    {
        return new RamlValidationService(loader, new RamlDocumentValidator());
    }

    public static RamlValidationService createDefault(ResourceLoader loader, NodeRuleFactoryExtension... extensions)
    {
        return new RamlValidationService(loader, new RamlDocumentValidator(new NodeRuleFactory(extensions)));
    }
}
