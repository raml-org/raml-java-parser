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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang.StringUtils;
import org.raml.emitter.RamlEmitter;
import org.raml.parser.builder.NodeBuilder;
import org.raml.parser.builder.TupleBuilder;
import org.raml.parser.completion.DefaultSuggestion;
import org.raml.parser.completion.NodeContext;
import org.raml.parser.completion.Suggestion;
import org.raml.parser.loader.DefaultResourceLoader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.nodes.Tag;

public class YamlDocumentSuggester implements NodeHandler
{


    private YamlDocumentBuilder builder;
    private int offset;
    private Stack<NodeContext> nodes;

    public YamlDocumentSuggester(YamlDocumentBuilder builder)
    {

        this.builder = builder;
        this.nodes = new Stack<NodeContext>();
    }

    public List<Suggestion> suggest(String topSection, String context)
    {
        return suggest(topSection, context, null);
    }

    public List<Suggestion> suggest(String topSection, String context, String bottomSection)
    {

        final List<Suggestion> result = new ArrayList<Suggestion>();

        topSection = topSection.trim();
        this.offset = topSection.length();
        if (offset == 0)
        {
            result.add(new DefaultSuggestion(RamlEmitter.VERSION));
            return result;
        }
        Yaml yamlParser = new Yaml();
        NodeVisitor nodeVisitor = new NodeVisitor(this, new DefaultResourceLoader());
        MappingNode rootNode = (MappingNode) yamlParser.compose(new StringReader(topSection));


        nodeVisitor.visitDocument(rootNode);

        int contextColumn = calculateContextColumn(context);

        NodeBuilder<?> parent = null;
        NodeContext parentNode = null;
        while (!nodes.isEmpty())
        {

            parentNode = popNode();
            int column = parentNode.getColumn();

            parent = (NodeBuilder) this.builder.getBuilderContext().pop();

            if (column < contextColumn)
            {
                break;
            }
        }
        if (!isContextInValue(context))
        {
            addKeySuggestions(context, result, parent, parentNode.getKeys());
        }
        else
        {
            //todo add value suggestions
        }

        Collections.sort(result);
        return result;
    }

    private void addKeySuggestions(String context, List<Suggestion> result, NodeBuilder<?> parent, List<String> existingKeys)
    {
        if (parent instanceof TupleBuilder)
        {
            Collection<TupleBuilder<?, ?>> childrenTupleBuilders = ((TupleBuilder<?, ?>) parent).getChildrenTupleBuilders();
            for (TupleBuilder<?, ?> childTupleBuilder : childrenTupleBuilders)
            {
                List<Suggestion> suggestions = childTupleBuilder.getHandler().getSuggestions();
                String contextTrimmed = context.trim();
                for (Suggestion suggestion : suggestions)
                {
                    if (suggestion.getLabel().startsWith(contextTrimmed) && !existingKeys.contains(suggestion.getLabel()))
                    {
                        result.add(suggestion);
                    }
                }
            }
        }
    }

    private boolean isContextInValue(String context)
    {
        return context.contains(":");
    }

    private int calculateContextColumn(String context)
    {
        int column = 0;
        while (column < context.length() && StringUtils.isWhitespace(context.charAt(column) + ""))
        {
            column++;
        }
        return column;
    }

    private void pushNode(Node node, MappingNode mappingNode)
    {
        //System.out.format("pushing %s node = %s\n", node.getNodeId(), NodeUtils.getNodeValue(node));
        nodes.push(new NodeContext(node.getStartMark().getColumn(), mappingNode));
    }

    private NodeContext popNode()
    {
        //System.out.println("popping node");
        return nodes.pop();
    }

    @Override
    public boolean onMappingNodeStart(MappingNode mappingNode, TupleType tupleType)
    {
        return builder.onMappingNodeStart(mappingNode, tupleType);
    }

    @Override
    public void onMappingNodeEnd(MappingNode mappingNode, TupleType tupleType)
    {
        builder.onMappingNodeEnd(mappingNode, tupleType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean onSequenceStart(SequenceNode node, TupleType tupleType)
    {
        pushNode(node, null);
        return builder.onSequenceStart(node, tupleType);
    }

    @Override
    public void onSequenceEnd(SequenceNode node, TupleType tupleType)
    {

        popNode();
        builder.onSequenceEnd(node, tupleType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onScalar(ScalarNode node, TupleType tupleType)
    {
        builder.onScalar(node, tupleType);
    }

    @Override
    public boolean onDocumentStart(MappingNode node)
    {
        pushNode(node, node);
        return builder.onDocumentStart(node);
    }

    @Override
    public void onDocumentEnd(MappingNode node)
    {

    }

    @Override
    public void onTupleEnd(NodeTuple nodeTuple)
    {
        Node valueNode = nodeTuple.getValueNode();
        if (validateOffset(valueNode))
        {
            popNode();
            builder.onTupleEnd(nodeTuple);
        }
    }

    private boolean validateOffset(Node valueNode)
    {
        return valueNode != null && valueNode.getEndMark().getIndex() < offset;
    }

    @Override
    public boolean onTupleStart(NodeTuple nodeTuple)
    {
        try
        {
            builder.onTupleStart(nodeTuple);
            MappingNode mapping = nodeTuple.getValueNode().getNodeId() == NodeId.mapping ? (MappingNode) nodeTuple.getValueNode() : null;
            pushNode(nodeTuple.getKeyNode(), mapping);
        }
        catch (Exception e)
        {
            return false;
        }
        return true;
    }

    @Override
    public void onSequenceElementStart(Node sequenceNode)
    {
        builder.onSequenceElementStart(sequenceNode);
    }

    @Override
    public void onSequenceElementEnd(Node sequenceNode)
    {
        builder.onSequenceElementEnd(sequenceNode);
    }

    @Override
    public void onCustomTagStart(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
    {
        builder.onCustomTagStart(tag, originalValueNode, nodeTuple);
    }

    @Override
    public void onCustomTagEnd(Tag tag, Node originalValueNode, NodeTuple nodeTuple)
    {
        builder.onCustomTagEnd(tag, originalValueNode, nodeTuple);
    }

    @Override
    public void onCustomTagError(Tag tag, Node node, String message)
    {
        builder.onCustomTagError(tag, node, message);
    }
}
