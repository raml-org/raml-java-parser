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


import java.util.List;

import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;

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

    public static ErrorNode createInvalidFacet(String type, String message)
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
        return new ErrorNode("Required property " + property + " cannot be made optional");
    }

    public static ErrorNode createCanNotOverrideCustomFacet(String facetName, String typeName)
    {
        return new ErrorNode("Custom facet " + facetName + " can not be set as it is already defined by " + typeName + ".");
    }

    public static ErrorNode createCanNotOverrideNativeFacet(String facetName)
    {
        return new ErrorNode("Custom facet " + facetName + " can not be defined as is already defined by this type.");
    }

    public static ErrorNode createCanNotOverrideProperty(String propertyName)
    {
        return new ErrorNode("Property " + propertyName + " can not be overwritten .");
    }

    public static ErrorNode createPropertyCanNotBeOfSchemaType(String propertyName)
    {
        return new ErrorNode("Property  " + propertyName + " can not have a schema type.");
    }

    public static ErrorNode createRecurrentTypeDefinition(String typeName)
    {
        return new ErrorNode("Recurrent type definition : " + typeName + ".");
    }
}
