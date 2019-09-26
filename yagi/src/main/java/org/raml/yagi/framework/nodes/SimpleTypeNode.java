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
package org.raml.yagi.framework.nodes;

public interface SimpleTypeNode<T> extends Node
{

    /**
     * @return the value of the node
     */
    T getValue();

    /**
     * in some cases the value of the node is different than the literal one
     * e.g:  (literal value) -&gt; (value)
     *       001 -&gt; 1
     *       0xF -&gt; 15
     * @return the literal value of the node
     *
     */
    String getLiteralValue();
}
