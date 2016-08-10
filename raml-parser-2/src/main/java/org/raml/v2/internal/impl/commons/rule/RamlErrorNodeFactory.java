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
package org.raml.v2.internal.impl.commons.rule;


import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.v2.internal.impl.commons.nodes.FacetNode;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

import java.util.List;

public class RamlErrorNodeFactory
{

    public static ErrorNode createInvalidAnnotationTarget(List<AnnotationTarget> allowedTargets, AnnotationTarget target)
    {
        return new ErrorNode("Annotation not allowed at target: " + target + ". Allowed targets are: " + allowedTargets);
    }

    public static ErrorNode createInvalidUriTemplate()
    {
        return new ErrorNode("Invalid uri template syntax");
    }

    public static ErrorNode createInvalidFacetState(String type, String message)
    {
        String prefix = "Invalid facets";
        if (type != null)
        {
            prefix += " for type " + type;
        }
        return new ErrorNode(prefix + ": " + message);
    }

    public static ErrorNode createInvalidRequiredFacet(String property)
    {
        return new ErrorNode("Required property '" + property + "' cannot be made optional");
    }

    public static ErrorNode createCanNotOverrideCustomFacet(String facetName, String typeName)
    {
        return new ErrorNode("Custom facet '" + facetName + "' cannot be set as it is already defined by " + typeName + ".");
    }

    public static ErrorNode createCanNotOverrideNativeFacet(String facetName)
    {
        return new ErrorNode("Custom facet '" + facetName + "' cannot be defined as is already defined by this type.");
    }

    public static ErrorNode createCanNotOverrideProperty(String propertyName)
    {
        return new ErrorNode("Property '" + propertyName + "' cannot be overwritten.");
    }

    public static ErrorNode createPropertyCanNotBeOfSchemaType(String propertyName)
    {
        return new ErrorNode("Property '" + propertyName + "' cannot have a schema type.");
    }

    public static ErrorNode createRecurrentTypeDefinition(String typeName)
    {
        return new ErrorNode("Recurrent type definition: " + typeName + ".");
    }

    public static ErrorNode createInvalidLibraryChaining(String value)
    {
        return new ErrorNode("Library references cannot be chained: " + value);
    }

    public static ErrorNode createInvalidFacetForType(FacetNode facetNode, String typeName)
    {
        return new ErrorNode("Facet '" + facetNode.getName() + "' cannot be applied to " + typeName);
    }

    public static Node createInvalidFormatValue(String value, String format)
    {
        return new ErrorNode(value + " is not a valid " + format + " value");
    }

}
