package org.raml.parser.rule;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;

public interface IRuleWithContext<V extends Node> {

	/**
	 * Called when we have parser context
	 * @param value
	 * @param ctx
	 * @return list of validation results
	 */
	List<ValidationResult> doValidateValue(V value,IParserContext ctx);
}
