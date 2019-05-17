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

import org.raml.model.Protocol;
import org.raml.model.Raml;
import org.raml.parser.resolver.TupleHandler;
import org.raml.parser.rule.ProtocolTupleHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

import java.util.Collection;
import java.util.Map;

/**
 * Created. There, you have it.
 */
public class ProtocolBuilder implements TupleBuilder<Node, Node>, SequenceBuilder {

    @Override
    public NodeBuilder getBuilderForTuple(NodeTuple tuple) {
        return null;
    }

    @Override
    public void buildKey(Object parent, Node tuple) {

    }

    @Override
    public TupleHandler getHandler() {

        return new ProtocolTupleHandler();
    }

    @Override
    public Collection<TupleBuilder<?, ?>> getChildrenTupleBuilders() {
        return null;
    }

    @Override
    public void setHandler(TupleHandler handler) {

    }

    @Override
    public void setChildrenTupleBuilders(Map nestedBuilders) {

    }

    @Override
    public Object buildValue(Object parent, Node node) {

        Raml raml = (Raml) parent;
        if ( node instanceof ScalarNode) {

            raml.getProtocols().add(Protocol.valueOf(((ScalarNode) node).getValue().toUpperCase()));
        }

        return parent;
    }

    @Override
    public void setParentNodeBuilder(NodeBuilder parentBuilder) {

    }

    @Override
    public NodeBuilder getItemBuilder() {
        return this;
    }
}
