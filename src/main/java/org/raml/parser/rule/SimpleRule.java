package org.raml.parser.rule;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.ScalarNode;


public class SimpleRule extends DefaultTupleRule<ScalarNode, ScalarNode>
{


    private static final String EMPTY_MESSAGE = "can not be empty";
    private static final String DUPLICATE_MESSAGE = "Duplicate";

    private ScalarNode keyNode;
    private ScalarNode valueNode;


    public SimpleRule(String fieldName)
    {
        super(fieldName, new DefaultScalarTupleHandler(ScalarNode.class, fieldName));
    }

    public static String getRuleEmptyMessage(String ruleName)
    {
        return ruleName + " " + EMPTY_MESSAGE;
    }

    public static String getDuplicateRuleMessage(String ruleName)
    {
        return DUPLICATE_MESSAGE + " " + ruleName;
    }


    @Override
    public List<ValidationResult> validateKey(ScalarNode key)
    {
        List<ValidationResult> validationResults = super.validateKey(key);
        if (wasAlreadyDefined())
        {
            validationResults.add(ValidationResult.createErrorResult(getDuplicateRuleMessage(getFieldName()), key.getStartMark(), key.getEndMark()));
        }
        setKeyNode(key);

        return validationResults;
    }

    @Override
    public List<ValidationResult> validateValue(ScalarNode node)
    {
        String value = node.getValue();
        if (StringUtils.isEmpty(value))
        {
            return Arrays.<ValidationResult>asList(ValidationResult.createErrorResult(getRuleEmptyMessage(getFieldName()), keyNode.getStartMark(), keyNode.getEndMark()));
        }
        setValueNode(node);
        return super.validateValue(node);
    }

    public boolean wasAlreadyDefined()
    {
        return keyNode != null;
    }

    public void setKeyNode(ScalarNode rulePresent)
    {
        this.keyNode = rulePresent;
    }

    public ScalarNode getKeyNode()
    {
        return keyNode;
    }

    public ScalarNode getValueNode()
    {
        return valueNode;
    }

    public void setValueNode(ScalarNode valueNode)
    {
        this.valueNode = valueNode;
    }

}
