/*
 * Copyright (c) MuleSoft, Inc.
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

import static org.raml.parser.utils.NodeUtils.isStandardTag;
import static org.raml.parser.visitor.TupleType.KEY;
import static org.raml.parser.visitor.TupleType.VALUE;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.TagResolver;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class NodeVisitor
{

    private NodeHandler nodeHandler;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    public NodeVisitor(NodeHandler nodeHandler, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        super();
        this.nodeHandler = nodeHandler;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
    }

    private void visitMappingNode(MappingNode mappingNode, TupleType tupleType)
    {
        nodeHandler.onMappingNodeStart(mappingNode, tupleType);
        if (tupleType == VALUE)
        {
            doVisitMappingNode(mappingNode);
        }
        nodeHandler.onMappingNodeEnd(mappingNode, tupleType);
    }

    private static class MappingNodeMerger extends SafeConstructor
    {

        void merge(MappingNode mappingNode)
        {
            flattenMapping(mappingNode);
        }
    }

    private void doVisitMappingNode(MappingNode mappingNode)
    {
        if (mappingNode.isMerged())
        {
            new MappingNodeMerger().merge(mappingNode);
        }
        List<NodeTuple> tuples = mappingNode.getValue();
        List<NodeTuple> updatedTuples = new ArrayList<NodeTuple>();
        for (NodeTuple nodeTuple : tuples)
        {
            Node keyNode = nodeTuple.getKeyNode();
            Node valueNode = nodeTuple.getValueNode();
            Node originalValueNode = valueNode;
            Tag tag = valueNode.getTag();
            TagResolver tagResolver = getTagResolver(tag);
            if (tagResolver != null)
            {
                valueNode = tagResolver.resolve(valueNode, resourceLoader, nodeHandler);
                nodeTuple = new NodeTuple(keyNode, valueNode);
            }
            else if (!isStandardTag(tag))
            {
                nodeHandler.onCustomTagError(tag, valueNode, "Unknown tag " + tag);
            }
            updatedTuples.add(nodeTuple);
            if (tagResolver != null)
            {
                nodeHandler.onCustomTagStart(tag, originalValueNode, nodeTuple);
            }
            nodeHandler.onTupleStart(nodeTuple);
            visit(keyNode, KEY);
            visit(valueNode, VALUE);
            nodeHandler.onTupleEnd(nodeTuple);
            if (tagResolver != null)
            {
                nodeHandler.onCustomTagEnd(tag, originalValueNode, nodeTuple);
            }

        }
        mappingNode.setValue(updatedTuples);
    }

    private TagResolver getTagResolver(Tag tag)
    {
        for (TagResolver resolver : tagResolvers)
        {
            if (resolver.handles(tag))
            {
                return resolver;
            }
        }
        return null;
    }

    public void visitDocument(MappingNode node)
    {
        nodeHandler.onDocumentStart(node);
        if (node != null)
        {
            doVisitMappingNode(node);
        }
        nodeHandler.onDocumentEnd(node);
    }

    private void visit(Node node, TupleType tupleType)
    {
        if (node.getNodeId() == NodeId.mapping)
        {
            visitMappingNode((MappingNode) node, tupleType);
        }
        else if (node.getNodeId() == NodeId.scalar)
        {
            visitScalar((ScalarNode) node, tupleType);
        }
        else if (node.getNodeId() == NodeId.sequence)
        {
            visitSequence((SequenceNode) node, tupleType);
        }
    }

    private void visitSequence(SequenceNode node, TupleType tupleType)
    {
        nodeHandler.onSequenceStart(node, tupleType);
        if (tupleType == VALUE)
        {
            List<Node> value = node.getValue();
            for (Node sequenceNode : value)
            {
                nodeHandler.onSequenceElementStart(sequenceNode);
                visit(sequenceNode, tupleType);
                nodeHandler.onSequenceElementEnd(sequenceNode);
            }
        }
        nodeHandler.onSequenceEnd(node, tupleType);
    }

    private void visitScalar(ScalarNode node, TupleType tupleType)
    {
        nodeHandler.onScalar(node, tupleType);
    }


}
