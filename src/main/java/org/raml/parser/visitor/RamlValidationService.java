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

    public RamlValidationService(ResourceLoader resourceLoader, RamlDocumentValidator ramlDocumentValidator, TagResolver... tagResolvers)
    {
        super(resourceLoader, tagResolvers, ramlDocumentValidator);
        validator = ramlDocumentValidator;
        validator.setResourceLoader(resourceLoader);
    }

    @Override
    protected List<ValidationResult> preValidation(MappingNode root)
    {
        List<ValidationResult> validationResults = validator.getTemplateResolver().init(root);
        validationResults.addAll(validator.getMediaTypeResolver().beforeDocumentStart(root));
        return validationResults;
    }

    public static RamlValidationService createDefault()
    {
        return createDefault(new DefaultResourceLoader());
    }

    public static RamlValidationService createDefault(ResourceLoader loader, NodeRuleFactoryExtension... extensions)
    {
        return new RamlValidationService(loader, new RamlDocumentValidator(new NodeRuleFactory(extensions)), new IncludeResolver());
    }
}
