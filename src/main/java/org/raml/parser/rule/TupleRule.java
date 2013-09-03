package org.raml.parser.rule;

import java.util.List;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleRule<K extends Node, V extends Node> extends NodeRule<V>
{

    List<ValidationResult> validateKey(K key);

    TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple);

    void setParentTupleRule(TupleRule<?, ?> parent);

    TupleRule<?, ?> getParentTupleRule();

    String getName();

    TupleRule<?, ?> getRuleByFieldName(String fieldName);

    void setNestedRules(Map<String, TupleRule<?, ?>> innerBuilders);

    void setHandler(TupleHandler tupleHandler);

    TupleHandler getHandler();

    void setRequired(boolean required);

    void setNodeRuleFactory(NodeRuleFactory nodeRuleFactory);

    K getKey();
}
