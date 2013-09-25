package org.raml.parser.visitor;

import java.lang.reflect.Field;
import java.util.List;

import org.raml.model.Raml;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.NodeRule;
import org.raml.parser.rule.NodeRuleFactory;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.nodes.MappingNode;

public class RamlDocumentValidator extends YamlDocumentValidator
{

    private TemplateResolver templateResolver;
    private ResourceLoader resourceLoader;

    public RamlDocumentValidator()
    {
        super(Raml.class);
    }

    public RamlDocumentValidator(NodeRuleFactory nodeRuleFactory)
    {
        super(Raml.class, nodeRuleFactory);
    }

    public TemplateResolver getTemplateResolver()
    {
        if (templateResolver == null)
        {
            templateResolver = new TemplateResolver(resourceLoader, this);
        }
        return templateResolver;
    }

    @Override
    public void onMappingNodeStart(MappingNode mappingNode)
    {
        super.onMappingNodeStart(mappingNode);
        NodeRule<?> rule = getRuleContext().peek();
        if (isResourceRule(rule))
        {
            List<ValidationResult> templateValidations = getTemplateResolver().resolve(mappingNode);
            getMessages().addAll(templateValidations);
        }
    }

    private boolean isResourceRule(NodeRule<?> rule)
    {
        try
        {
            Field valueType = rule.getClass().getDeclaredField("valueType");
            valueType.setAccessible(true);
            return ((Class) valueType.get(rule)).getName().equals("org.raml.model.Resource");
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

    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
}
