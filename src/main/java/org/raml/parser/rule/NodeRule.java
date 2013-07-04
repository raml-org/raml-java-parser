package org.raml.parser.rule;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;

/**
 *
 */
public interface NodeRule<V extends Node>
{

    /**
     * Validates the given value
     *
     * @param value The value to validate
     * @return
     */
    List<ValidationResult> validateValue(V value);

    /**
     * Called when the ruled was ended to verify all mandatory fields are present
     *
     * @return
     */
    List<ValidationResult> onRuleEnd();
}
