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
package org.raml.v2.internal.impl.v10;

import static org.raml.v2.internal.impl.RamlBuilder.FIRST_PHASE;
import static org.raml.v2.internal.impl.RamlBuilder.GRAMMAR_PHASE;
import static org.raml.v2.internal.impl.v10.RamlFragment.Extension;
import static org.raml.v2.internal.impl.v10.RamlFragment.Overlay;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.RamlNodeParser;
import org.raml.v2.internal.framework.phase.GrammarPhase;
import org.raml.v2.internal.framework.phase.Phase;
import org.raml.v2.internal.framework.phase.TransformationPhase;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.commons.phase.ExtensionsMerger;
import org.raml.v2.internal.impl.commons.phase.IncludeResolver;
import org.raml.v2.internal.impl.commons.phase.RamlFragmentGrammarTransformer;
import org.raml.v2.internal.impl.commons.phase.ReferenceResolverTransformer;
import org.raml.v2.internal.impl.commons.phase.ResourceTypesTraitsTransformer;
import org.raml.v2.internal.impl.commons.phase.SchemaValidationTransformer;
import org.raml.v2.internal.impl.commons.phase.StringTemplateExpressionTransformer;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.AnnotationValidationPhase;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjectionPhase;
import org.raml.v2.internal.utils.StreamUtils;

public class Raml10Builder
{

    public Node build(String stringContent, RamlFragment fragment, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        Node rootNode = RamlNodeParser.parse(resourceLocation, stringContent);
        if (rootNode == null)
        {
            return ErrorNodeFactory.createEmptyDocument();
        }
        boolean applyExtension = false;
        if ((fragment == Extension || fragment == Overlay) && maxPhaseNumber > FIRST_PHASE)
        {
            applyExtension = true;
            maxPhaseNumber = GRAMMAR_PHASE;
        }
        final List<Phase> phases = createPhases(resourceLoader, fragment);
        rootNode = runPhases(rootNode, phases, maxPhaseNumber);
        if (applyExtension && rootNode.findDescendantsWith(ErrorNode.class).isEmpty())
        {
            rootNode = applyExtension(rootNode, resourceLoader, resourceLocation);
        }
        return rootNode;
    }

    private Node runPhases(Node rootNode, List<Phase> phases, int maxPhaseNumber)
    {
        for (int i = 0; i < phases.size(); i++)
        {
            if (i < maxPhaseNumber)
            {
                Phase phase = phases.get(i);
                rootNode = phase.apply(rootNode);
                List<ErrorNode> errorNodes = rootNode.findDescendantsWith(ErrorNode.class);
                if (!errorNodes.isEmpty())
                {
                    break;
                }
            }
        }
        return rootNode;
    }

    private Node applyExtension(Node extensionNode, ResourceLoader resourceLoader, String resourceLocation) throws IOException
    {
        StringNode baseRef = (StringNode) extensionNode.get("extends");
        InputStream baseStream = resourceLoader.fetchResource(baseRef.getValue());
        String baseContent = StreamUtils.toString(baseStream);
        Node baseNode = new RamlBuilder().build(baseContent, resourceLoader, resourceLocation);

        if (baseNode.findDescendantsWith(ErrorNode.class).isEmpty())
        {
            ExtensionsMerger.merge(baseNode, extensionNode);
            List<Phase> phases = createPhases(resourceLoader, getFragment(baseContent));
            baseNode = runPhases(baseNode, phases, Integer.MAX_VALUE);
        }
        return baseNode;
    }

    private RamlFragment getFragment(String content)
    {
        try
        {
            return RamlHeader.parse(content).getFragment();
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            // already validated by builder
            throw new RuntimeException("Unreachable code");
        }
    }

    private List<Phase> createPhases(ResourceLoader resourceLoader, RamlFragment fragment)
    {
        // The first phase expands the includes.
        final TransformationPhase includePhase = new TransformationPhase(new IncludeResolver(resourceLoader), new StringTemplateExpressionTransformer());

        final TransformationPhase ramlFragmentsValidator = new TransformationPhase(new RamlFragmentGrammarTransformer(resourceLoader));

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final Raml10Grammar raml10Grammar = new Raml10Grammar();

        final GrammarPhase grammarPhase = new GrammarPhase(fragment.getRule(raml10Grammar));
        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // sugar
        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???
        final TransformationPhase libraryLink = new TransformationPhase(new LibraryLinkingTransformation(resourceLoader));

        final TransformationPhase referenceCheck = new TransformationPhase(new ReferenceResolverTransformer());

        // Applies resourceTypes and Traits Library
        final TransformationPhase resourcePhase = new TransformationPhase(new ResourceTypesTraitsTransformer(raml10Grammar));

        // Run grammar again to re-validate tree

        final AnnotationValidationPhase annotationValidationPhase = new AnnotationValidationPhase(resourceLoader);

        final MediaTypeInjectionPhase mediaTypeInjection = new MediaTypeInjectionPhase();

        // Schema Types example validation

        final TransformationPhase schemaValidationPhase = new TransformationPhase(new SchemaValidationTransformer(resourceLoader));

        final ExampleValidationPhase exampleValidationPhase = new ExampleValidationPhase(resourceLoader);

        return Arrays.asList(includePhase,
                ramlFragmentsValidator,
                grammarPhase,
                libraryLink,
                referenceCheck,
                resourcePhase,
                annotationValidationPhase,
                mediaTypeInjection,
                grammarPhase,
                schemaValidationPhase,
                exampleValidationPhase);

    }
}
