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
package org.raml.v2.internal.impl.v08;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.commons.phase.DuplicatedPathsTransformer;
import org.raml.v2.internal.impl.commons.phase.IncludeResolver;
import org.raml.v2.internal.impl.commons.phase.ResourceTypesTraitsTransformer;
import org.raml.v2.internal.impl.commons.phase.StringTemplateExpressionTransformer;
import org.raml.v2.internal.impl.v08.grammar.Raml08Grammar;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.snakeyaml.NodeParser;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;

public class Raml08Builder
{

    public Node build(String stringContent, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        Node rootNode = NodeParser.parse(resourceLoader, resourceLocation, stringContent);
        final List<Phase> phases = createPhases(resourceLoader);
        for (int i = 0; i < phases.size(); i++)
        {
            if (i < maxPhaseNumber)
            {
                Phase phase = phases.get(i);
                rootNode = phase.apply(rootNode);
                List<ErrorNode> errorNodes = rootNode.findDescendantsWith(ErrorNode.class);
                if (!errorNodes.isEmpty())
                {
                    return rootNode;
                }
            }
        }
        return rootNode;
    }


    private List<Phase> createPhases(ResourceLoader resourceLoader)
    {
        // The first phase expands the includes.
        final TransformationPhase first = new TransformationPhase(new IncludeResolver(resourceLoader), new StringTemplateExpressionTransformer());
        // Overlays and extensions.

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        Raml08Grammar raml08Grammar = new Raml08Grammar();
        final GrammarPhase second = new GrammarPhase(raml08Grammar.raml());
        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???

        // Applies resourceTypes and Traits Library
        final TransformationPhase third = new TransformationPhase(new ResourceTypesTraitsTransformer(raml08Grammar));

        // Check duplicate paths
        final TransformationPhase duplicatedPaths = new TransformationPhase(new DuplicatedPathsTransformer());

        // Schema Types example validation
        return Arrays.asList(first, second, third, duplicatedPaths);

    }
}
