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
package org.raml.parser.builder;

import java.util.Collection;
import java.util.Map;

import org.raml.parser.resolver.TupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;

public interface TupleBuilder<K extends Node, V extends Node> extends NodeBuilder<V>
{

    /**
     * Returns the node child builder that can handle the specified NodeTuple.
     *
     * @param tuple The tuple to whom builder will handle
     * @return The node builder
     */
    NodeBuilder getBuilderForTuple(NodeTuple tuple);

    /**
     * Build the parent object with the given key
     * @param parent The object to be modifed
     * @param tuple The node of the key
     */
    void buildKey(Object parent, K tuple);

    /**
     * The handler that specifies whether or not this build can handle a given NodeTuple
     * @return The Handler
     */
    TupleHandler getHandler();


    /**
     * The builders for the children NodeTuples
     *
     * @return The collection of TupleBuilder
     */
    Collection<TupleBuilder<?, ?>> getChildrenTupleBuilders();

    void setHandler(TupleHandler handler);

    void setChildrenTupleBuilders(Map<String, TupleBuilder<?, ?>> nestedBuilders);

}
