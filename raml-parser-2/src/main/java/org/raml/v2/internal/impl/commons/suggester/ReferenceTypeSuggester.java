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
package org.raml.v2.internal.impl.commons.suggester;

import org.raml.yagi.framework.nodes.KeyValueNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.snakeyaml.SYStringNode;
import org.raml.yagi.framework.suggester.DefaultSuggestion;
import org.raml.yagi.framework.suggester.Suggestion;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

public class ReferenceTypeSuggester extends ReferenceSuggester
{

    public ReferenceTypeSuggester(String referenceKey)
    {
        super(referenceKey);
    }

    @Nonnull
    public List<Suggestion> getSuggestions(Node node)
    {
        List<Suggestion> result = super.getSuggestions(node);
        String self = getSelfType(node);
        if (!result.isEmpty())
        {
            Iterator<Suggestion> suggestionIterator = result.iterator();
            while (suggestionIterator.hasNext())
            {
                if (suggestionIterator.next().getValue().equals(self))
                {
                    suggestionIterator.remove();
                }
            }
        }
        result.add(new DefaultSuggestion("object", null, "Object"));
        return result;
    }

    private String getSelfType(Node node)
    {
        return ((SYStringNode) ((KeyValueNode) node.getParent().getParent().getParent()).getKey()).getValue();
    }
}
