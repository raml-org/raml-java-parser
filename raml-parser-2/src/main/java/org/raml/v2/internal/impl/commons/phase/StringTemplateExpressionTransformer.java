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
package org.raml.v2.internal.impl.commons.phase;

import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.impl.commons.nodes.TemplateExpressionNode;
import org.raml.v2.internal.impl.commons.nodes.StringTemplateNode;
import org.raml.yagi.framework.phase.Transformer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringTemplateExpressionTransformer implements Transformer
{
    public static Pattern TEMPLATE_PATTERN = Pattern.compile("<<(.+?)>>");

    @Override
    public boolean matches(Node node)
    {
        if (node instanceof StringNode)
        {
            final String value = ((StringNode) node).getValue();
            return TEMPLATE_PATTERN.matcher(value).find();
        }
        return false;
    }

    @Override
    public Node transform(Node node)
    {
        if (node instanceof StringTemplateNode)
        {
            return node;
        }
        final String value = ((StringNode) node).getValue();
        final StringTemplateNode stringTemplateNode = new StringTemplateNode(value);
        final Matcher templateMatcher = TEMPLATE_PATTERN.matcher(value);
        final Position startPosition = node.getStartPosition();
        // The previous template expression end position.
        int previousEndPosition = 0;
        while (templateMatcher.find())
        {
            final int start = templateMatcher.start();
            final int end = templateMatcher.end();
            if (start > previousEndPosition)
            {
                final StringNodeImpl stringNode = new StringNodeImpl(value.substring(previousEndPosition, start));
                stringNode.setStartPosition(startPosition.rightShift(previousEndPosition));
                stringNode.setEndPosition(startPosition.rightShift(start));
                stringTemplateNode.addChild(stringNode);
            }
            final TemplateExpressionNode expressionNode = new TemplateExpressionNode(templateMatcher.group(1));
            expressionNode.setStartPosition(startPosition.rightShift(templateMatcher.start(1)));
            expressionNode.setEndPosition(startPosition.rightShift(templateMatcher.end(1)));
            stringTemplateNode.addChild(expressionNode);
            previousEndPosition = end;
        }

        if (value.length() > previousEndPosition)
        {
            final StringNodeImpl stringNode = new StringNodeImpl(value.substring(previousEndPosition, value.length()));
            stringNode.setStartPosition(startPosition.rightShift(previousEndPosition));
            stringNode.setEndPosition(startPosition.rightShift(value.length()));
            stringTemplateNode.addChild(stringNode);
        }

        return stringTemplateNode;
    }
}
