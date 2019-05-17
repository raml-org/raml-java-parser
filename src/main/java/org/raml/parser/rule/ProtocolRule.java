package org.raml.parser.rule;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class ProtocolRule implements TupleRule<Node, Node>, SequenceRule {

    protected Map<String, TupleRule<?, ?>> rules = new HashMap<String, TupleRule<?, ?>>();
    private TupleRule<?, ?> parent;
    private String name;
    private TupleHandler tupleHandler;
    private NodeRuleFactory nodeRuleFactory;
    private Node key;

    public ProtocolRule() {
    }

    public ProtocolRule(Map<String, TupleRule<?, ?>> rules, TupleRule<?, ?> parent, String name, TupleHandler tupleHandler, NodeRuleFactory nodeRuleFactory, Node key) {
        this.rules = rules;
        this.parent = parent;
        this.name = name;
        this.tupleHandler = tupleHandler;
        this.nodeRuleFactory = nodeRuleFactory;
        this.key = key;
    }

    @Override
    public List<ValidationResult> validateKey(Node key) {
        this.key = key;
        return new ArrayList<ValidationResult>();
    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple) {
        return new UnknownTupleRule<Node, Node>(nodeTuple.getKeyNode().toString());
    }

    @Override
    public void setParentTupleRule(TupleRule<?, ?> parent) {

        this.parent = parent;
    }

    @Override
    public TupleRule<?, ?> getParentTupleRule() {
        return parent;
    }

    @Override
    public TupleRule<?, ?> getRootTupleRule() {

        TupleRule<?, ?> parentTupleRule = getParentTupleRule();
        if (parentTupleRule == null)
        {
            return null;
        }
        while (parentTupleRule.getParentTupleRule() != null)
        {
            parentTupleRule = parentTupleRule.getParentTupleRule();
        }
        return parentTupleRule;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

        this.name = name;
    }

    @Override
    public TupleRule<?, ?> getRuleByFieldName(String fieldName) {
        return rules.get(fieldName);
    }

    @Override
    public void setNestedRules(Map<String, TupleRule<?, ?>> innerBuilders) {

        this.rules = innerBuilders;
    }

    @Override
    public void setHandler(TupleHandler tupleHandler) {

        this.tupleHandler = tupleHandler;
    }

    @Override
    public TupleHandler getHandler() {
        return new ProtocolTupleHandler();
    }

    @Override
    public void setRequired(boolean required) {

    }

    @Override
    public void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory) {

        this.nodeRuleFactory = nodeRuleFactory;
    }

    @Override
    public Node getKey() {
        return key;
    }

    @Override
    public void setValueType(Type valueType) {

    }

    @Override
    public TupleRule<?, ?> deepCopy()
    {
        checkClassToCopy(DefaultTupleRule.class);
        ProtocolRule copy = new ProtocolRule(rules, parent, name, tupleHandler, nodeRuleFactory, key);
        return copy;
    }

    protected void checkClassToCopy(Class<?> clazz)
    {
        if (! this.getClass().equals(clazz))
        {
            throw new RuntimeException(this.getClass() + " must implement deepCopy");
        }
    }

    @Override
    public List<ValidationResult> validateValue(Node value) {
        return new ArrayList<ValidationResult>();
    }

    @Override
    public List<ValidationResult> onRuleEnd() {
        return new ArrayList<ValidationResult>();
    }

    @Override
    public NodeRule<?> getItemRule() {
        return this;
    }

}
