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
package org.raml.yagi.framework.suggester;

public interface Suggestion extends Comparable<Suggestion>
{

    /**
     * @return the label displayed as the suggestion
     */
    String getLabel();

    /**
     * @return short description of the suggestion
     */
    String getDescription();

    /**
     * @return actual value added by the editor
     */
    String getValue();

    /**
     * @return the prefix that must be append to the value when inserting into de document
     */
    String getPrefix();

    /**
     * Creates a new Suggestion based on this suggestion but changing the description with the specified
     * @param description The new description
     * @return A new suggestion with the new description.
     */
    Suggestion withDescription(String description);

    /**
     * Creates a new Suggestion based on this suggestion but changing the value with the specified
     * @param value The new value
     * @return A new suggestion with the new value.
     */
    Suggestion withValue(String value);

    /**
     * Creates a new Suggestion based on this but changing the prefix
     * @param prefix The prefix
     * @return A new suggestion with the prefix changed.
     */
    Suggestion withPrefix(String prefix);
}
