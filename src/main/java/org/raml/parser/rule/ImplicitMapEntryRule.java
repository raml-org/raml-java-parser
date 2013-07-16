/**
 *
 */
package org.raml.parser.rule;

import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ImplicitMapEntryRule extends DefaultTupleRule<ScalarNode, MappingNode>
{


    private final Class valueType;

    public ImplicitMapEntryRule(String fieldName, Class valueType)
    {
        super(fieldName, new DefaultScalarTupleHandler(Node.class, fieldName));
        this.valueType = valueType;

    }

    @Override
    public TupleRule<?, ?> getRuleForTuple(NodeTuple nodeTuple)
    {
        if (rules.isEmpty())
        {
            addRulesFor(valueType);
        }
        return super.getRuleForTuple(nodeTuple);
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        List<ValidationResult> validationResults = super.onRuleEnd();
        rules.clear();
        return validationResults;
    }
}
