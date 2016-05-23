/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.v2.internal.utils;

import org.raml.v2.api.loader.ResourceLoader;

public class NodeValidator
{

    private ResourceLoader resourceLoader;

    public NodeValidator(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }
    //
    // public PayloadValidationResultNode validatePayload(Node type, String payload)
    // {
    // PayloadValidationResultNode payloadValidationResultNode = new PayloadValidationResultNode(new PayloadNode(type, payload));
    // this.validatePayload(payloadValidationResultNode);
    // return payloadValidationResultNode;
    // }
    //
    // private void validatePayload(PayloadValidationResultNode payload)
    // {
    // if (payload.getValue() instanceof PayloadNode)
    // {
    // this.validateExample((PayloadNode) payload.getValue());
    // }
    // }
    //
    // public void validateExample(ExampleTypeNode example)
    // {
    // if (example.getTypeNode() instanceof ObjectTypeNode)
    // {
    // validateType(example);
    // }
    // else
    // {
    // validateScalar(example);
    // }
    // }
    //
    // private void validateType(ExampleTypeNode example)
    // {
    //
    // ObjectTypeNode type = (ObjectTypeNode) example.getTypeNode();
    // if (type != null)
    // {
    // Node schemaType = NodeUtils.getType(type);
    // Rule rule = getVisitRule(example, type, schemaType);
    // replaceWithError(example, validateWithRule(example, rule));
    // }
    // }
    //
    // private Node validateWithRule(ExampleTypeNode example, Rule rule)
    // {
    // Node transform = null;
    // if (example instanceof MultipleExampleTypeNode || example.isArrayExample())
    // {
    // visitChildrenWithRule(example, rule);
    // }
    // else
    // {
    // if (NodeUtils.isStringNode(example.getSource()) && !(rule instanceof JsonSchemaValidationRule || rule instanceof XmlSchemaValidationRule))
    // {
    // Node transformed = RamlNodeParser.parse(example.getStartPosition().getPath(), ((StringNode) example.getSource()).getValue());
    // if (transformed != null)
    // {
    // transform = rule.apply(transformed);
    // }
    // }
    // else
    // {
    // transform = rule.apply(example);
    // }
    // }
    // return transform;
    // }
    //
    // private void validateScalar(ExampleTypeNode example)
    // {
    // Node transform = null;
    // if (example instanceof MultipleExampleTypeNode)
    // {
    // validateMultipleExampleNode(example);
    // }
    // else
    // {
    // transform = validateSingleExampleNode(example);
    // }
    // replaceWithError(example, transform);
    // }
    //
    // private void replaceWithError(ExampleTypeNode example, Node transform)
    // {
    // if (NodeUtils.isErrorResult(transform))
    // {
    // example.replaceWith(transform);
    // }
    // }
    //
    // private Rule getVisitRule(ExampleTypeNode example, ObjectTypeNode type, Node schemaType)
    // {
    // Rule rule = null;
    // if (SchemaGenerator.isSchemaNode(schemaType))
    // {
    // rule = getRuleForSchema(schemaType, rule);
    // }
    // else
    // rule = getRuleForType(example, type);
    // return rule;
    // }
    //
    // private Rule getRuleForType(ExampleTypeNode example, ObjectTypeNode type)
    // {
    // Rule rule;
    // if (!type.getInheritedProperties().isEmpty())
    // {
    // List<Rule> inheritanceRules = getInheritanceRules(example, type);
    // rule = new AnyOfRule(inheritanceRules);
    // }
    // else
    // {
    // rule = example.visitProperties(new TypeToRuleVisitor(), type.getProperties(), type.isAllowAdditionalProperties(), true);
    // }
    // return rule;
    // }
    //
    // private Rule getRuleForSchema(Node schemaType, Rule rule)
    // {
    // if (SchemaGenerator.isJsonSchemaNode(schemaType))
    // {
    // rule = new JsonSchemaValidationRule(schemaType);
    // }
    // else if (SchemaGenerator.isXmlSchemaNode(schemaType))
    // {
    // rule = new XmlSchemaValidationRule(schemaType, resourceLoader);
    // }
    // return rule;
    // }
    //
    // private void visitChildrenWithRule(ExampleTypeNode example, Rule rule)
    // {
    // Node transform;
    // for (Node childExample : example.getChildren())
    // {
    // Node exampleValue;
    // if (childExample instanceof KeyValueNode)
    // {
    // exampleValue = ((KeyValueNodeImpl) childExample).getValue();
    // }
    // else if (childExample instanceof ObjectNode)
    // {
    // exampleValue = childExample;
    // }
    // else
    // {
    // break;
    // }
    // if (!(rule instanceof XmlSchemaValidationRule || rule instanceof JsonSchemaValidationRule) && exampleValue instanceof StringNode)
    // {
    // Node parsedExample = RamlNodeParser.parse(exampleValue.getStartPosition().getPath(), ((StringNode) exampleValue).getValue());
    // Node exampleParent = exampleValue.getParent();
    // exampleParent.removeChild(exampleValue);
    // exampleParent.addChild(parsedExample);
    // parsedExample.setSource(exampleValue);
    // exampleValue = parsedExample;
    // }
    //
    // transform = rule.apply(exampleValue);
    // exampleValue.replaceWith(transform);
    // }
    // }
    //
    // private Node validateSingleExampleNode(ExampleTypeNode example)
    // {
    // Rule rule;
    // Node transform = null;
    // rule = example.visit(new TypeToRuleVisitor());
    // if (example.getSource() != null)
    // {
    // transform = rule.apply(example.getSource());
    // }
    // return transform;
    // }
    //
    // private void validateMultipleExampleNode(ExampleTypeNode example)
    // {
    // Rule rule;
    // Node transform;
    // for (Node childExample : example.getChildren())
    // {
    // Node exampleValue;
    // if (childExample instanceof KeyValueNode)
    // {
    // exampleValue = ((KeyValueNodeImpl) childExample).getValue();
    // }
    // else
    // {
    // break;
    // }
    // rule = example.visit(new TypeToRuleVisitor());
    // transform = rule.apply(exampleValue);
    // exampleValue.replaceWith(transform);
    // }
    // }
    //
    // private List<Rule> getInheritanceRules(ExampleTypeNode example, ObjectTypeNode type)
    // {
    // List<Rule> rules = Lists.newArrayList();
    // for (InheritedPropertiesInjectedNode inheritedProperties : type.getInheritedProperties())
    // {
    // rules.add(example.visitProperties(new TypeToRuleVisitor(), inheritedProperties.getProperties(), type.isAllowAdditionalProperties(), false));
    // }
    // return rules;
    // }

}
