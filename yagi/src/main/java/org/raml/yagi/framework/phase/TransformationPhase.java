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
package org.raml.yagi.framework.phase;

import org.raml.yagi.framework.nodes.Node;

import java.util.Arrays;
import java.util.List;

/**
 * Applies a list of Transformers in a pipeline way
 */
public class TransformationPhase implements Phase
{

    private List<Transformer> transformers;

    public TransformationPhase(Transformer... transformers)
    {
        this.transformers = Arrays.asList(transformers);
    }

    @Override
    public Node apply(Node tree)
    {
        // first pass may replace child nodes
        Node result = tree;
        for (Transformer transformer : transformers)
        {
            if (transformer.matches(result))
            {
                result = transformer.transform(result);
            }
        }
        if (tree != result && tree.getParent() != null)
        {
            tree.replaceWith(result);
        }
        for (Node node : result.getChildren())
        {
            apply(node);
        }
        return result;
    }
}
