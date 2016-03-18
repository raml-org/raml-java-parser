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
package org.raml.parser.resolver;

import java.util.ArrayList;
import java.util.List;

import org.raml.parser.completion.KeySuggestion;
import org.raml.parser.completion.Suggestion;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class ResponseHandler implements TupleHandler
{

    @Override
    public boolean handles(NodeTuple tuple)
    {
        if (tuple.getKeyNode() instanceof ScalarNode)
        {
            ScalarNode keyNode = (ScalarNode) tuple.getKeyNode();
            try
            {
                int status = Integer.parseInt(keyNode.getValue());
                return status > 0;
            }
            catch (NumberFormatException nfe)
            {
                //ignore
            }
        }
        return false;
    }

    @Override
    public List<Suggestion> getSuggestions()
    {
        int[] statuses = {200, 201, 204, 400, 401, 404, 409, 500};

        List<Suggestion> suggestions = new ArrayList<Suggestion>(statuses.length);
        for (int status : statuses)
        {
            suggestions.add(new KeySuggestion(String.valueOf(status)));
        }
        return suggestions;
    }

}
