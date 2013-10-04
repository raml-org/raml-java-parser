package org.raml.parser.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.raml.model.parameter.UriParameter;

public class ParamRule extends PojoTupleRule
{


    public ParamRule(String fieldName, NodeRuleFactory nodeRuleFactory)
    {
        super(fieldName, UriParameter.class);
        setNodeRuleFactory(nodeRuleFactory);
    }

    @Override
    public void addRulesFor(Class<?> pojoClass)
    {
        super.addRulesFor(pojoClass);
        SimpleRule typeRule = (SimpleRule) getRuleByFieldName("type");

        rules.put("minLength", new EnumModifierRule("minLength", Arrays.asList("string"), typeRule));
        rules.put("maxLength", new EnumModifierRule("maxLength", Arrays.asList("string"), typeRule));
        rules.put("minimum", new EnumModifierRule("minimum", Arrays.asList("integer", "number"), typeRule));
        rules.put("maximum", new EnumModifierRule("maximum", Arrays.asList("integer", "number"), typeRule));
    }

    @Override
    public List<ValidationResult> onRuleEnd()
    {
        return Collections.emptyList();
    }

}
