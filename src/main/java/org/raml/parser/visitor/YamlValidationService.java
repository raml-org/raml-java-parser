package org.raml.parser.visitor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.NodeRuleFactoryExtension;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

public class YamlValidationService
{

    private List<ValidationResult> errorMessage;
    private YamlValidator[] yamlValidators;
    private ResourceLoader resourceLoader;

    public YamlValidationService(YamlValidator... yamlValidators)
    {
        this(new DefaultResourceLoader(), yamlValidators);
    }

    public YamlValidationService(ResourceLoader resourceLoader, YamlValidator... yamlValidators)
    {
        this.resourceLoader = resourceLoader;
        this.yamlValidators = yamlValidators;
        this.errorMessage = new ArrayList<ValidationResult>();
    }


    public List<ValidationResult> validate(String content)
    {
        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(new CompositeHandler(yamlValidators), resourceLoader);
            for (Node data : yamlParser.composeAll(new StringReader(content)))
            {
                if (data instanceof MappingNode)
                {
                    nodeVisitor.visitDocument((MappingNode) data);
                }
                else
                {
                    //   errorMessage.add(ValidationResult.createErrorResult(EMPTY_DOCUMENT_MESSAGE));
                }
            }

        }
        catch (YAMLException ex)
        {
            // errorMessage.add(ValidationResult.createErrorResult(ex.getMessage()));
        }

        for (YamlValidator yamlValidator : yamlValidators)
        {
            errorMessage.addAll(yamlValidator.getMessages());
        }
        return errorMessage;
    }

    public static YamlValidationService createDefault(Class<?> clazz)
    {
        return new YamlValidationService(new YamlDocumentValidator(clazz));
    }

    public static YamlValidationService createDefault(Class<?> clazz, ResourceLoader loader)
    {
        return new YamlValidationService(loader, new YamlDocumentValidator(clazz));
    }

    public static YamlValidationService createDefault(Class<?> clazz, ResourceLoader loader, NodeRuleFactoryExtension... extensions)
    {
        return new YamlValidationService(loader, new YamlDocumentValidator(clazz, new NodeRuleFactory(extensions)));
    }

}
