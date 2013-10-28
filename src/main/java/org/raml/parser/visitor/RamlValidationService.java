package org.raml.parser.visitor;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.IncludeResolver;
import org.raml.parser.tagresolver.JacksonTagResolver;
import org.raml.parser.tagresolver.JaxbTagResolver;
import org.raml.parser.tagresolver.PojoValidatorTagResolver;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlValidationService extends YamlValidationService
{

    private RamlDocumentValidator validator;

    public RamlValidationService(ResourceLoader resourceLoader, RamlDocumentValidator ramlDocumentValidator, TagResolver... tagResolvers)
    {
        super(resourceLoader, ramlDocumentValidator, defaultResolver(tagResolvers));
        validator = ramlDocumentValidator;
        validator.setResourceLoader(resourceLoader);
    }

    private static TagResolver[] defaultResolver(TagResolver[] tagResolvers)
    {
        TagResolver[] defaultResolvers = new TagResolver[] {
                new IncludeResolver(),
                new PojoValidatorTagResolver()
        };
        return (TagResolver[]) ArrayUtils.addAll(defaultResolvers, tagResolvers);
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

    public static RamlValidationService createDefault(ResourceLoader loader, TagResolver... tagResolvers)
    {
        return createDefault(loader, new NodeRuleFactory(), tagResolvers);
    }

    public static RamlValidationService createDefault(ResourceLoader loader, NodeRuleFactory nodeRuleFactory, TagResolver... tagResolvers)
    {
        return new RamlValidationService(loader, new RamlDocumentValidator(nodeRuleFactory), tagResolvers);
    }

}
