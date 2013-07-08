package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.resolver.DefaultTupleHandler;
import org.yaml.snakeyaml.nodes.Node;

public class UnknownTupleRule<K extends Node, V extends Node> extends DefaultTupleRule<K, V>
{

    public UnknownTupleRule(String fieldName)
    {
        super(fieldName, new DefaultTupleHandler());
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {       
        final List<ValidationResult> result = new ArrayList<ValidationResult>();
        result.add(ValidationResult.createErrorResult("Unknown key "+getFieldName().replaceAll("(.*value=?)(\\w+)(.*)", "$2"),getKey().getStartMark() , getKey().getEndMark()));
        return result;
    }
    
}
