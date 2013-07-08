package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public class DefaultTupleRule<K extends Node, V extends Node> implements TupleRule<K, V>
{

    private static final String IS_MISSING = "is missing";
    protected Map<String, TupleRule<?, ?>> rules;
    private TupleRule<?, ?> parent;
    private TupleHandler tupleHandler;
    private boolean required;
    private K key;
    private String fieldName;

    public DefaultTupleRule(String fieldName, TupleHandler handler)
    {
        this.fieldName = fieldName;
        rules = new HashMap<String, TupleRule<?, ?>>();
        this.tupleHandler = handler;
    }

    public static String getMissingRuleMessage(String ruleName)
    {
        return ruleName + " " + IS_MISSING;
    }

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }


    @Override
    public void setNestedRules(Map<String, TupleRule<?, ?>> rules)
    {
        this.rules = rules;
    }

    @Override
    public void setHandler(TupleHandler tupleHandler)
    {
        this.tupleHandler = tupleHandler;
    }

    @Override
    public boolean handles(NodeTuple touple)
    {
        return tupleHandler.handles(touple);
    }

    @Override
    public List<ValidationResult> validateKey(K key)
    {
        this.key = key;
        return new ArrayList<ValidationResult>(Arrays.asList(ValidationResult.okResult()));
    }

    @Override
    public List<ValidationResult> validateValue(V key)
    {
        return new ArrayList<ValidationResult>(Arrays.asList(ValidationResult.okResult()));
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        if (isRequired() && !wasAlreadyDefined())
        {
            result.add(ValidationResult.createErrorResult(getMissingRuleMessage(fieldName)));
        }

        for (TupleRule<?, ?> rule : rules.values())
        {
            List<ValidationResult> onRuleEnd = rule.onRuleEnd();
            result.addAll(onRuleEnd);
        }
        return result;
    }

    private boolean wasAlreadyDefined()
    {
        return key != null;
    }
    
    public K getKey(){
        return key;
    }

    public void addRulesFor(Class<?> pojoClass)
    {
        new TupleRuleFactory().addRulesTo(pojoClass, this);
    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        for (TupleRule<?, ?> rule : rules.values())
        {
            if (rule.handles(nodeTuple))
            {
                return rule;
            }
        }
        return new UnknownTupleRule<Node,Node>(nodeTuple.getKeyNode().toString());
    }

    @Override
    public void setParentTupleRule(TupleRule<?, ?> parent)
    {

        this.parent = parent;
    }

    @Override
    public TupleRule<?, ?> getRuleByFieldName(String fieldName)
    {
        return rules.get(fieldName);
    }


    public TupleRule<?, ?> getParent()
    {
        return parent;
    }
}
