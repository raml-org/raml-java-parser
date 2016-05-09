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
package org.raml.v2.internal.impl.commons.nodes;

import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.nodes.ExecutableNode;
import org.raml.v2.internal.framework.nodes.ExecutionContext;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNodeImpl;
import org.raml.v2.internal.utils.Inflector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class TemplateExpressionNode extends StringNodeImpl implements ExecutableNode
{
    public TemplateExpressionNode(@Nonnull String value)
    {
        super(value);
    }

    public TemplateExpressionNode(TemplateExpressionNode node)
    {
        super(node);
    }

    @Nullable
    public String getVariableName()
    {
        final StringTokenizer expressionTokens = getExpressionTokens();
        return expressionTokens.hasMoreTokens() ? expressionTokens.nextToken() : null;
    }

    public Node execute(ExecutionContext context)
    {
        final StringTokenizer expressionTokens = getExpressionTokens();
        String result = null;
        if (expressionTokens.hasMoreTokens())
        {
            final String token = expressionTokens.nextToken().trim();
            if (context.containsVariable(token))
            {
                result = context.getVariable(token);
            }
            else
            {
                return ErrorNodeFactory.createInvalidTemplateParameterExpression(this, token);
            }
        }
        while (expressionTokens.hasMoreTokens())
        {
            final String token = expressionTokens.nextToken().trim();
            if (token.startsWith("!"))
            {
                try
                {
                    Method method = Inflector.class.getMethod(token.substring(1), String.class);
                    result = String.valueOf(method.invoke(null, result));
                }
                catch (Exception e)
                {
                    return ErrorNodeFactory.createInvalidTemplateFunctionExpression(this, token);
                }
            }
            else
            {
                return ErrorNodeFactory.createInvalidTemplateFunctionExpression(this, token);
            }
        }

        return new StringNodeImpl(result);

    }

    private StringTokenizer getExpressionTokens()
    {
        final String value = getValue();
        return new StringTokenizer(value, "|");
    }

    @Nonnull
    @Override
    public Node copy()
    {
        return new TemplateExpressionNode(this);
    }
}
