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

import static org.raml.v2.api.model.v10.RamlFragment.Extension;
import static org.raml.v2.api.model.v10.RamlFragment.Overlay;
import static org.raml.v2.internal.impl.RamlBuilder.FIRST_PHASE;
import static org.raml.v2.internal.impl.RamlBuilder.GRAMMAR_PHASE;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.snakeyaml.RamlNodeParser;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.phase.ExtensionsMerger;
import org.raml.v2.internal.impl.commons.phase.IncludeResolver;
import org.raml.v2.internal.impl.commons.phase.RamlFragmentGrammarTransformer;
import org.raml.v2.internal.impl.commons.phase.ReferenceResolverTransformer;
import org.raml.v2.internal.impl.commons.phase.ResourceTypesTraitsTransformer;
import org.raml.v2.internal.impl.commons.phase.SchemaValidationTransformer;
import org.raml.v2.internal.impl.commons.phase.StringTemplateExpressionTransformer;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.AnnotationValidationPhase;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjectionPhase;
import org.raml.v2.internal.utils.ResourcePathUtils;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.v2.internal.utils.TreeDumper;

public class Raml10Builder
{

    public Node build(String stringContent, RamlFragment fragment, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        Node rootNode = RamlNodeParser.parse(resourceLoader, resourceLocation, stringContent);
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
            rootNode = applyExtension(rootNode, resourceLoader, resourceLocation, fragment);
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
                String dump = new TreeDumper().dump(rootNode);
                checkDumpPhases(i, phase, dump);
                List<ErrorNode> errorNodes = rootNode.findDescendantsWith(ErrorNode.class);
                if (!errorNodes.isEmpty())
                {
                    break;
                }
            }
        }
        return rootNode;
    }

    private void checkDumpPhases(int i, Phase phase, String dump)
    {
        if (Boolean.getBoolean("dump.phases"))
        {
            System.out.println("===============================================================");
            System.out.println("After phase = " + i + " --- " + phase.getClass());
            System.out.println("---------------------------------------------------------------");
            System.out.println(dump);
            System.out.println("---------------------------------------------------------------");
        }
    }

    private Node applyExtension(Node extensionNode, ResourceLoader resourceLoader, String resourceLocation, RamlFragment fragment) throws IOException
    {
        StringNode baseRef = (StringNode) extensionNode.get("extends");
        String baseLocation = ResourcePathUtils.toAbsoluteLocation(resourceLocation, baseRef.getValue());
        InputStream baseStream = resourceLoader.fetchResource(baseLocation);
        if (baseStream == null)
        {
            return ErrorNodeFactory.createBaseRamlNotFound(baseRef.getValue());
        }
        String baseContent = StreamUtils.toString(baseStream);
        Node baseNode = new RamlBuilder().build(baseContent, resourceLoader, resourceLocation);

        if (baseNode.findDescendantsWith(ErrorNode.class).isEmpty())
        {
            new ExtensionsMerger(fragment == Overlay).merge(baseNode, extensionNode);
            if (baseNode.findDescendantsWith(ErrorNode.class).isEmpty())
            {
                // run all phases on merged document
                List<Phase> phases = createPhases(resourceLoader, getFragment(baseContent));
                baseNode = runPhases(baseNode, phases, Integer.MAX_VALUE);
            }
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
        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(fragment));

        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // sugar
        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???
        final TransformationPhase libraryLink = new TransformationPhase(new LibraryLinkingTransformation(resourceLoader));

        final TransformationPhase referenceCheck = new TransformationPhase(new ReferenceResolverTransformer());

        // Applies resourceTypes and Traits Library
        final TransformationPhase resourcePhase = new TransformationPhase(new ResourceTypesTraitsTransformer(new Raml10Grammar()));

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
