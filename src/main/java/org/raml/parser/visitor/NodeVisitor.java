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

import static org.raml.parser.utils.NodeUtils.isStandardTag;
import static org.raml.parser.visitor.TupleType.KEY;
import static org.raml.parser.visitor.TupleType.VALUE;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.loader.ResourceLoaderAware;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.tagresolver.ContextPathAware;
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

    public static final Tag LOOP_TAG = new Tag("!loop");
    private NodeHandler nodeHandler;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;
    private Deque<String> loopDetector = new ArrayDeque<String>();
    private ContextPath contextPath = new ContextPath();

    public NodeVisitor(NodeHandler nodeHandler, ResourceLoader resourceLoader, TagResolver... tagResolvers)
    {
        super();
        this.nodeHandler = nodeHandler;
        this.resourceLoader = resourceLoader;
        this.tagResolvers = tagResolvers;
        initializeContextPathAware(tagResolvers);
        initializeResourceLoaderAware();
        SchemaCompiler.getInstance().init(contextPath, resourceLoader);
    }

    private void initializeResourceLoaderAware()
    {
        if (nodeHandler instanceof ResourceLoaderAware)
        {
            ((ResourceLoaderAware) nodeHandler).setResourceLoader(resourceLoader);
        }
    }

    private void initializeContextPathAware(TagResolver[] tagResolvers)
    {
        for (TagResolver tagResolver : tagResolvers)
        {
            if (tagResolver instanceof ContextPathAware)
            {
                ((ContextPathAware) tagResolver).setContextPath(contextPath);
            }
        }
        if (nodeHandler instanceof ContextPathAware)
        {
            ((ContextPathAware) nodeHandler).setContextPath(contextPath);
        }
    }

    private void visitMappingNode(MappingNode mappingNode, TupleType tupleType)
    {
        if (checkLoop(mappingNode))
        {
            nodeHandler.onCustomTagError(LOOP_TAG, mappingNode, "Circular reference detected");
            return;
        }
        boolean keepOnVisiting = nodeHandler.onMappingNodeStart(mappingNode, tupleType);
        if (tupleType == VALUE && keepOnVisiting)
        {
            doVisitMappingNode(mappingNode);
        }
        nodeHandler.onMappingNodeEnd(mappingNode, tupleType);
        if (mappingNode.getStartMark() != null)
        {
            loopDetector.pop();
        }
    }

    /**
     * @return true if two mapping nodes in the same file
     *         have the same start mark index
     */
    private boolean checkLoop(Node node)
    {
        if (node.getStartMark() == null)
        {
            return false;
        }

        String index = contextPath.peek().getIncludeName() + node.getStartMark().getIndex();
        if (loopDetector.contains(index))
        {
            return true;
        }
        loopDetector.push(index);
        return false;
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
            Node originalValueNode = nodeTuple.getValueNode();

            Tag tag = originalValueNode.getTag();
            TagResolver currentTagResolver = getTagResolver(tag);
            Node resolvedNode = resolveTag(tag, currentTagResolver, originalValueNode);
            if (originalValueNode != resolvedNode)
            {
                nodeTuple = new NodeTuple(keyNode, resolvedNode);
            }
            updatedTuples.add(nodeTuple);
            boolean processTuple = nodeHandler.onTupleStart(nodeTuple);
            if (!processTuple)
            {
                continue;
            }
            visit(keyNode, KEY);
            visitResolvedNode(originalValueNode, resolvedNode, currentTagResolver);
            nodeHandler.onTupleEnd(nodeTuple);

        }
        mappingNode.setValue(updatedTuples);
    }

    private Node resolveTag(Tag tag, TagResolver tagResolver, Node valueNode)
    {
        if (tagResolver != null)
        {
            valueNode = tagResolver.resolve(valueNode, resourceLoader, nodeHandler);
        }
        else if (!isStandardTag(tag))
        {
            nodeHandler.onCustomTagError(tag, valueNode, "Unknown tag " + tag);
        }
        return valueNode;
    }

    private void visitResolvedNode(Node originalValueNode, Node resolvedNode, TagResolver tagResolver)
    {
        Tag tag = originalValueNode.getTag();
        boolean tagResolved = tagResolver != null;
        if (tagResolved)
        {
            tagResolver.beforeProcessingResolvedNode(tag, originalValueNode, resolvedNode);
            nodeHandler.onCustomTagStart(tag, originalValueNode, resolvedNode);
        }
        visit(resolvedNode, VALUE);
        if (tagResolved)
        {
            nodeHandler.onCustomTagEnd(tag, originalValueNode, resolvedNode);
            tagResolver.afterProcessingResolvedNode(tag, originalValueNode, resolvedNode);
        }
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
        boolean keepOnVisitingDocument = nodeHandler.onDocumentStart(node);
        if (node != null && keepOnVisitingDocument)
        {
            if (contextPath.size() == 0)
            {
                contextPath.pushRoot("");
            }
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
        boolean keepVisitingElements = nodeHandler.onSequenceStart(node, tupleType);
        if (tupleType == VALUE && keepVisitingElements)
        {
            List<Node> value = node.getValue();
            for (int i = 0; i < value.size(); i++)
            {
                Node originalNode = value.get(i);
                TagResolver currentTagResolver = getTagResolver(originalNode.getTag());
                Node resolvedNode = resolveTag(originalNode.getTag(), currentTagResolver, originalNode);
                if (originalNode != resolvedNode)
                {
                    node.getValue().remove(i);
                    node.getValue().add(i, resolvedNode);
                }
                nodeHandler.onSequenceElementStart(resolvedNode);
                visitResolvedNode(originalNode, resolvedNode, currentTagResolver);
                nodeHandler.onSequenceElementEnd(resolvedNode);
            }
        }
        nodeHandler.onSequenceEnd(node, tupleType);
    }

    private void visitScalar(ScalarNode node, TupleType tupleType)
    {
        nodeHandler.onScalar(node, tupleType);
    }


}
