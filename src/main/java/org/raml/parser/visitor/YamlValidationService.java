package org.raml.parser.visitor;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

public class YamlValidationService
{

    private List<ValidationResult> errorMessage;
    private YamlValidator[] yamlValidators;
    private ResourceLoader resourceLoader;
    private NodeHandler nodeHandler;

    protected YamlValidationService(YamlValidator... yamlValidators)
    {
        this(new DefaultResourceLoader(), yamlValidators);
    }

    protected YamlValidationService(ResourceLoader resourceLoader, YamlValidator... yamlValidators)
    {
        this.resourceLoader = resourceLoader;
        this.yamlValidators = yamlValidators;
        this.errorMessage = new ArrayList<ValidationResult>();
        this.nodeHandler = new CompositeHandler(yamlValidators);
    }

    public List<ValidationResult> validate(String content)
    {
        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(nodeHandler, resourceLoader);
            Node root = yamlParser.compose(new StringReader(content));
            errorMessage.addAll(preValidation((MappingNode) root));
            if (errorMessage.isEmpty() && root.getNodeId() == NodeId.mapping)
            {
                nodeVisitor.visitDocument((MappingNode) root);
            }
        }
        catch (MarkedYAMLException mye)
        {
            errorMessage.add(ValidationResult.createErrorResult(mye.getProblem(), mye.getProblemMark(), null));
        }
        catch (YAMLException ex)
        {
            errorMessage.add(ValidationResult.createErrorResult(ex.getMessage()));
        }

        for (YamlValidator yamlValidator : yamlValidators)
        {
            errorMessage.addAll(yamlValidator.getMessages());
        }
        return errorMessage;
    }

    protected List<ValidationResult> preValidation(MappingNode root)
    {
        //template method
        return new ArrayList<ValidationResult>();
    }

}
