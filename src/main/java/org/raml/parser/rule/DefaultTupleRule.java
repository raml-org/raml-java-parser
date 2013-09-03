package org.raml.parser.rule;

import java.util.ArrayList;
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
    private String name;
    private NodeRuleFactory nodeRuleFactory;


    public DefaultTupleRule(String name, TupleHandler handler, NodeRuleFactory nodeRuleFactory)
    {
        this(name, handler);
        this.setNodeRuleFactory(nodeRuleFactory);
    }

    public DefaultTupleRule(String name, TupleHandler handler)
    {
        this.name = name;
        this.rules = new HashMap<String, TupleRule<?, ?>>();
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

    @Override
    public void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory)
    {
        this.nodeRuleFactory = nodeRuleFactory;
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
    public TupleHandler getHandler()
    {
        return tupleHandler;
    }


    @Override
    public List<ValidationResult> validateKey(K key)
    {
        this.key = key;
        return new ArrayList<ValidationResult>();
    }

    @Override
    public List<ValidationResult> validateValue(V key)
    {
        return new ArrayList<ValidationResult>();
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> result = new ArrayList<ValidationResult>();
        if (isRequired() && !wasAlreadyDefined())
        {
            result.add(ValidationResult.createErrorResult(getMissingRuleMessage(name)));
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

    @Override
    public K getKey()
    {
        return key;
    }

    public void addRulesFor(Class<?> pojoClass)
    {
        nodeRuleFactory.addRulesTo(pojoClass, this);
    }

    public NodeRuleFactory getNodeRuleFactory()
    {
        return nodeRuleFactory;
    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        for (TupleRule<?, ?> rule : rules.values())
        {
            if (rule.getHandler().handles(nodeTuple))
            {
                return rule;
            }
        }
        return new UnknownTupleRule<Node, Node>(nodeTuple.getKeyNode().toString());
    }

    @Override
    public void setParentTupleRule(TupleRule<?, ?> parent)
    {

        this.parent = parent;
    }

    @Override
    public String getName()
    {
        return name;
    }

    @Override
    public TupleRule<?, ?> getRuleByFieldName(String fieldName)
    {
        return rules.get(fieldName);
    }

    @Override
    public TupleRule<?, ?> getParentTupleRule()
    {
        return parent;
    }
}
