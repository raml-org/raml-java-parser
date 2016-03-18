/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.completion;

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
     * If there are siblings to the current auto-complete context
     *   the right indentation value is returned, otherwise -1
     *
     * @return right indentation in spaces
     */
    int getIndentation();

    /**
     * sets the right indentation for the suggestion
     *
     * @param indentation
     */
    void setIndentation(int indentation);

}
