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
package org.raml.v2.internal.framework.grammar.rule;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.suggester.RamlParsingContext;
import org.raml.v2.internal.framework.suggester.Suggestion;
import org.raml.v2.internal.utils.DateUtils;

public class DateValueRule extends Rule
{

    private String dateType;
    private String rfc;


    public DateValueRule(String dateType, String rfc)
    {
        this.dateType = dateType;
        this.rfc = rfc;
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, RamlParsingContext context)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode;
    }

    @Override
    public Node apply(@Nonnull Node node)
    {
        if (matches(node))
        {
            if (DateUtils.isValidDate(((StringNode) node).getValue(), this.dateType, this.rfc))
            {
                return node;
            }
            else
            {
                return ErrorNodeFactory.createInvalidDateValue(((StringNode) node).getValue(), this.dateType, this.rfc);
            }
        }
        else
        {
            return ErrorNodeFactory.createInvalidNode(node);
        }
    }

    @Override
    public String getDescription()
    {
        return "Multiple of value";
    }
}
