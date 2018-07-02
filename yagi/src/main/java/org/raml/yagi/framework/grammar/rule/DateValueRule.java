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
package org.raml.yagi.framework.grammar.rule;


import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.suggester.ParsingContext;
import org.raml.yagi.framework.suggester.Suggestion;
import org.raml.yagi.framework.util.DateType;
import org.raml.yagi.framework.util.DateUtils;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class DateValueRule extends Rule
{

    private DateType dateType;
    private String rfc = "rfc3339";


    public DateValueRule(DateType dateType, String rfc)
    {
        this.dateType = dateType;
        if (rfc != null)
        {
            this.rfc = rfc;
        }
    }

    @Nonnull
    @Override
    public List<Suggestion> getSuggestions(Node node, ParsingContext context)
    {
        return Collections.emptyList();
    }

    @Override
    public boolean matches(@Nonnull Node node)
    {
        return node instanceof StringNode && DateUtils.isValidDate(((StringNode) node).getValue(), this.dateType, this.rfc);
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
                return ErrorNodeFactory.createInvalidDateValue(((StringNode) node).getValue(), this.dateType.name(), this.rfc);
            }
        }
        else
        {
            if (node instanceof StringNode)
            {
                return ErrorNodeFactory.createInvalidDateValue(((StringNode) node).getValue(), this.dateType.name(), this.rfc);
            }
            return ErrorNodeFactory.createInvalidNode(node);
        }
    }

    @Override
    public String getDescription()
    {
        return "Multiple of value";
    }
}
