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
package org.raml.v2.api.model.v10.datamodel;

import java.util.List;

import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.api.model.v10.common.Annotable;
import org.raml.v2.api.model.v10.declarations.AnnotationTarget;
import org.raml.v2.api.model.v10.system.types.MarkdownString;
import org.raml.v2.api.model.v10.system.types.AnnotableStringType;


public interface TypeDeclaration extends Annotable
{

    /**
     * Name of the parameter
     **/
    String name();


    /**
     * The displayName attribute specifies the type display name. It is a friendly name used only for  display or documentation purposes. If displayName is not specified, it defaults to the element's key (the name of the property itself).
     **/
    AnnotableStringType displayName();


    /**
     * A base type which the current type extends, or more generally a type expression.
     **/
    String type();


    /**
     * The list of inherited types
     */
    List<TypeDeclaration> parentTypes();


    /**
     * Provides default value for a property
     **/
    String defaultValue();


    /**
     * An example of this type instance represented as string or yaml map&#47;sequence. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the examples property is present.
     **/
    ExampleSpec example();


    /**
     * An example of this type instance represented as string. This can be used, e.g., by documentation generators to generate sample values for an object of this type. Cannot be present if the example property is present.
     **/
    List<ExampleSpec> examples();


    /**
     * Sets if property is optional or not
     **/
    Boolean required();


    /**
     * A longer, human-friendly description of the type
     **/
    MarkdownString description();


    /**
     * Restrictions on where annotations of this type can be applied. If this property is specified, annotations of this type may only be applied on a property corresponding to one of the target names specified as the value of this property.
     **/
    List<AnnotationTarget> allowedTargets();


    /**
     * Validates <tt>payload</tt> against the type/schema defined
     *
     * @param payload the payload to be validated
     * @return the list of errors if any or an empty list if validation succeeded
     */
    List<ValidationResult> validate(String payload);


    /**
     * Gets the list of user-defined facets
     */
    List<TypeDeclaration> facets();


    XMLFacetInfo xml();


    String toXmlSchema();

}
