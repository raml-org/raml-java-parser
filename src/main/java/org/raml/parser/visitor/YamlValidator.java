package org.raml.parser.visitor;

import java.util.List;

import org.raml.parser.rule.ValidationResult;

public interface YamlValidator extends NodeHandler
{

    List<ValidationResult> getMessages();
}
