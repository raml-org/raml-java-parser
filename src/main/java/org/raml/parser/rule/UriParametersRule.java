package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.raml.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class UriParametersRule extends DefaultTupleRule<ScalarNode, MappingNode>
{


    private List<ValidationResult> errors;
    private List<String> parameters;
    private ScalarNode keyNode;
    private static final String DUPLICATE_MESSAGE = "Duplicate";

    public UriParametersRule()
    {
        super("uriParameters", new DefaultScalarTupleHandler(MappingNode.class, "uriParameters"));

        this.errors = new ArrayList<ValidationResult>();
        this.parameters = new ArrayList<String>();
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        return errors;
    }


    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = new ArrayList<ValidationResult>();
        if (wasAlreadyDefined())
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage("uriParameters"), key.getStartMark(), key.getEndMark()));
        }
        validationResults.addAll(super.validateKey(key));
        if (validationResults.size() == 1 && validationResults.get(0).equals(ValidationResult.okResult()))
        {
            setKeyNode(key);
        }
        return validationResults;
    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        Node keyNode = nodeTuple.getKeyNode();
        String paramName;
        if (keyNode instanceof ScalarNode)
        {            
            paramName = ((ScalarNode) keyNode).getValue();
            if (paramName.equals("version"))
            {
                errors.add(ValidationResult.createErrorResult("'" + paramName + "'" + " can not be declared, it is a reserved URI parameter.", keyNode.getStartMark(), keyNode.getEndMark()));
            }
            else if (getUriRule().getParameters().contains(paramName))
            {
                parameters.add(paramName);  
                return new ParamRule(paramName);
            }
            else
            {
                errors.add(ValidationResult.createErrorResult("Parameter '" + paramName + "' not declared in baseUri", keyNode.getStartMark(), keyNode.getEndMark()));
            }
        }
        else
        {
            errors.add(ValidationResult.createErrorResult("Invalid element", keyNode.getStartMark(), keyNode.getEndMark()));
        }
        
        return new DefaultTupleRule<>(keyNode.toString(), new DefaultTupleHandler());
    }

    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    public static String getDuplicateRuleMessage(String ruleName)
    {
        return DUPLICATE_MESSAGE + " " + ruleName;
    }

    public BaseUriRule getUriRule()
    {
        return (BaseUriRule) getParent().getRuleByFieldName("baseUri");
    }


}
