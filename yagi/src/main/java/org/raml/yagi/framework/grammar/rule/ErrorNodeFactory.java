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
package org.raml.yagi.framework.grammar.rule;

import org.apache.commons.lang.StringUtils;
import org.raml.yagi.framework.nodes.EmptyErrorNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.ReferenceNode;
import org.raml.yagi.framework.util.NodeSelector;

import java.io.IOException;
import java.util.Collection;
import java.util.Set;

public class ErrorNodeFactory
{

    public static ErrorNode createUnexpectedKey(Node key, Collection<String> options)
    {
        return new ErrorNode("Unexpected key '" + key + "'. Options are : " + StringUtils.join(options, " or "));
    }

    public static ErrorNode createInvalidArrayElement(Node child)
    {
        return new ErrorNode("Invalid array element " + child + ".");
    }

    public static ErrorNode createInvalidNode(Node child)
    {
        return new ErrorNode("Invalid element " + child + ".");
    }

    public static ErrorNode createInvalidRootElement(Node rootNode, String expected)
    {
        return new ErrorNode("Invalid root node " + rootNode + ". Expected : " + expected + ".");
    }

    public static ErrorNode createInvalidTemplateFunctionExpression(Node node, String token)
    {
        return new ErrorNode("Invalid template function expression " + token);
    }

    public static ErrorNode createInvalidTemplateParameterExpression(Node node, String token)
    {
        return new ErrorNode("Cannot resolve parameter " + token);
    }

    public static Node createRequiredValueNotFound(Node node, Rule keyRule)
    {
        final ErrorNode errorNode = new ErrorNode("Missing required field " + keyRule.getDescription());
        errorNode.setSource(node);
        return errorNode;
    }

    public static Node createInvalidType(Node node, NodeType type)
    {
        return new ErrorNode("Invalid type " + node.getType() + ", expected " + type);
    }

    public static Node createInvalidFragmentName(String fragmentText)
    {
        return new ErrorNode("Invalid fragment name '" + fragmentText + "'");
    }

    public static EmptyErrorNode createEmptyDocument()
    {
        return new EmptyErrorNode();
    }

    public static Node createUnsupportedVersion(String version)
    {
        return new ErrorNode("Unsupported version " + version + "");
    }

    public static Node createInvalidHeader(String header)
    {
        return new ErrorNode("Invalid header declaration " + header);
    }

    public static Node createInvalidInput(IOException ioe)
    {
        return new ErrorNode("Error while reading the input. Reason " + ioe.getMessage());
    }

    public static Node createInvalidMaxLength(int maxLength)
    {
        return new ErrorNode("Expected max length " + maxLength);
    }

    public static Node createInvalidMaxItems(int maxItems)
    {
        return new ErrorNode("Expected max items " + maxItems);
    }

    public static Node createInvalidMinItems(int minItems)
    {
        return new ErrorNode("Expected min items " + minItems);
    }

    public static Node createInvalidMaxProperties(int maxProperties)
    {
        return new ErrorNode("Expected max properties " + maxProperties);
    }

    public static Node createInvalidMinProperties(int minProperties)
    {
        return new ErrorNode("Expected min properties " + minProperties);
    }

    public static Node createInvalidMinLength(int minLength)
    {
        return new ErrorNode("Expected min length " + minLength);
    }

    public static Node createInvalidMinimumValue(Number minimumValue)
    {
        return new ErrorNode("Expected minimum value " + minimumValue);
    }

    public static Node createInvalidDivisorValue()
    {
        return new ErrorNode("Can not divide by 0");
    }

    public static Node createInvalidMultipleOfValue(Number multipleOfValue)
    {
        return new ErrorNode("Expected a multiple of " + multipleOfValue);
    }

    public static Node createInvalidMaximumValue(Number maximumValue)
    {
        return new ErrorNode("Expected maximum value " + maximumValue);
    }

    public static Node createInvalidRangeValue(Number minimumValue, Number maximumValue)
    {
        return new ErrorNode("Expected number between " + minimumValue + " and " + maximumValue);
    }

    public static Node createMissingField(String selector)
    {
        return new ErrorNode("Missing field " + selector);
    }

    public static Node createMissingAnnotationType(String type)
    {
        return new ErrorNode("Missing Annotation Type '" + type + "'");
    }

    public static Node createInvalidValue(Node node, String expected)
    {
        return new ErrorNode("Invalid value '" + node + "'. Expected " + expected);
    }

    public static Node createInvalidSiblingsValue(Node node, Set<String> siblings)
    {
        return new ErrorNode("Invalid node '" + node + "'. Node not expected when one of " + siblings + " is present.");
    }

    public static Node createInvalidJsonExampleNode(String error)
    {
        return new ErrorNode("Error validating JSON. Error: " + error);
    }

    public static Node createInvalidXmlExampleNode(String error)
    {
        return new ErrorNode("Error validating XML. Error: " + error);
    }

    public static Node createInvalidSchemaNode(String error)
    {
        return new ErrorNode("Error validating Schema. Error: " + error);
    }

    public static ErrorNode createInvalidType(String typeName)
    {
        return new ErrorNode("Invalid type name: " + typeName);
    }

    public static ErrorNode createInvalidNumberOfProperties(String comparator, Integer expected, Integer actual)
    {
        return new ErrorNode("Expected " + comparator + " number of properties to be: " + expected + " but was: " + actual);
    }

    public static ErrorNode createInvalidDateValue(String dateValue, String dateFormat, String rfc)
    {
        return new ErrorNode("Provided value " + dateValue + " is not compliant with the format " + dateFormat + " provided in " + rfc);
    }

    public static ErrorNode createInvalidReferenceNode(ReferenceNode refNode)
    {
        return new ErrorNode("Invalid reference '" + refNode.getRefName() + "'");
    }

    public static ErrorNode createNonexistentReferenceTraitError(ReferenceNode traitReference)
    {
        return new ErrorNode("Reference to nonexistent trait '" + traitReference.getRefName() + "'");
    }

    public static ErrorNode createNonexistentReferenceResourceTypeError(ReferenceNode resourceTypeReference)
    {
        return new ErrorNode("Reference to nonexistent resource type '" + resourceTypeReference.getRefName() + "'");
    }

    public static ErrorNode createInvalidTypeExpressionSyntax(String message, String expression, int location)
    {
        return new ErrorNode("Invalid type expression syntax: \"" + expression + "\". Caused by : " + message + " at character : " + location);
    }

    public static ErrorNode createInvalidOverlayNode(Node overlayNode)
    {
        String label = NodeSelector.selectStringValue("../[0]", overlayNode);
        if ("value".equals(label))
        {
            String parentKey = NodeSelector.selectStringValue("../../../[0]", overlayNode);
            if (parentKey != null)
            {
                label = parentKey + "." + label;
            }
        }
        return new ErrorNode("Invalid overlay node. Cannot override node: " + label);
    }

    public static Node createBaseRamlNotFound(String location)
    {
        return new ErrorNode("Base RAML not found: " + location);
    }

}
