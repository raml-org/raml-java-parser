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
package org.raml.parser.visitor;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public interface NodeHandler
{

    /**
     * This method is call when a mapping node 'event' is reached
     *
     * @param mappingNode The mapping node
     * @param tupleType   If is part of Key or A Value in the container Node.
     * @return should keep on visiting it's children
     */
    boolean onMappingNodeStart(MappingNode mappingNode, TupleType tupleType);

    void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType);

    /**
     * This method is call when a sequence node 'event' is reached
     *
     * @param sequenceNode The sequenceNode node
     * @param tupleType    If is part of Key or A Value in the container Node.
     * @return should keep on visiting it's elements
     * @see NodeHandler#onSequenceElementEnd(org.yaml.snakeyaml.nodes.Node)
     */
    boolean onSequenceStart(SequenceNode sequenceNode, TupleType tupleType);

    void onSequenceEnd(SequenceNode node, TupleType tupleType);

    void onScalar(ScalarNode node, TupleType tupleType);

    /**
     * This method is call when a document node 'event' is reached
     *
     * @param documentNode The documentNode node
     * @return should keep on visiting it's children
     */
    boolean onDocumentStart(MappingNode documentNode);

    void onDocumentEnd(MappingNode node);

    void onTupleEnd(NodeTuple nodeTuple);

    boolean onTupleStart(NodeTuple nodeTuple);

    void onSequenceElementStart(Node sequenceNode);

    void onSequenceElementEnd(Node sequenceNode);

    void onCustomTagStart(Tag tag, Node originalValueNode, Node node);

    void onCustomTagEnd(Tag tag, Node originalValueNode, Node node);

    void onCustomTagError(Tag tag, Node node, String message);
}
