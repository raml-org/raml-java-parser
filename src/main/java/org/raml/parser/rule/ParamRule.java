
package org.raml.parser.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.raml.parser.resolver.DefaultScalarTupleHandler;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

/**
 * This Rule handles each parameter
 * <p/>
 * %TAG ! tag:raml.org,0.1: --- title: Salesforce Chatter Communities REST API
 * version: v28.0 baseUri: https://{communityDomain}.force.com/{communityPath}
 * uriParameters: communityDomain: name: Community Domain type: string communityPath:
 * name: Community Path type: string pattern: ^[a-zA-Z0-9][-a-zA-Z0-9]*$
 * minimumLength: 1
 * <p/>
 * A new ParamRule will be created for communityPath and communityDomain
 *
 * @author seba
 */
public class ParamRule extends DefaultTupleRule<ScalarNode, Node>
{


    public ParamRule()
    {
        super(null, new DefaultScalarTupleHandler(MappingNode.class, null));
        EnumSimpleRule typeRule = new EnumSimpleRule("type", Arrays.asList("string", "number", "integer", "date"));
        rules.put("type", typeRule);
        List<String> validTypes = Arrays.asList("y", "yes",
                                                "YES", "t", "true", "TRUE", "n", "no", "NO", "f", "false", "FALSE");
        EnumSimpleRule requiredFieldRule = new EnumSimpleRule("required", validTypes);
        rules.put("required", requiredFieldRule);
        rules.put("name", new SimpleRule("name"));
        rules.put("minLength", new EnumModifierRule("minLength", Arrays.asList("string"), typeRule));
        rules.put("maxLength", new EnumModifierRule("maxLength", Arrays.asList("string"), typeRule));
        rules.put("minimum", new EnumModifierRule("minimum", Arrays.asList("integer", "number"), typeRule));
        rules.put("maximum", new EnumModifierRule("maximum", Arrays.asList("integer", "number"), typeRule));
        rules.put("default", new SimpleRule("default"));
    }


    @Override
    public List<ValidationResult> onRuleEnd()
    {
        return Collections.emptyList();
    }


}
