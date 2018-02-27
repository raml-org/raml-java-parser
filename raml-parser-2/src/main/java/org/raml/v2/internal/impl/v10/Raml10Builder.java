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

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.api.model.v10.RamlFragment;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.nodes.RamlVersionAnnotation;
import org.raml.v2.internal.impl.commons.phase.DuplicatedPathsTransformer;
import org.raml.v2.internal.impl.commons.phase.ExtensionsMerger;
import org.raml.v2.internal.impl.commons.phase.IncludeResolver;
import org.raml.v2.internal.impl.commons.phase.RamlFragmentGrammarTransformer;
import org.raml.v2.internal.impl.commons.phase.RamlFragmentLibraryLinkingTransformer;
import org.raml.v2.internal.impl.commons.phase.ResourceTypesTraitsTransformer;
import org.raml.v2.internal.impl.commons.phase.SchemaValidationTransformer;
import org.raml.v2.internal.impl.commons.phase.StringTemplateExpressionTransformer;
import org.raml.v2.internal.impl.commons.phase.TypeValidationPhase;
import org.raml.v2.internal.impl.commons.phase.UnusedParametersTransformer;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.AnnotationValidationPhase;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjectionPhase;
import org.raml.v2.internal.impl.v10.phase.ReferenceResolverTransformer;
import org.raml.v2.internal.utils.RamlNodeUtils;
import org.raml.v2.internal.utils.RamlTreeNodeDumper;
import org.raml.v2.internal.utils.ResourcePathUtils;
import org.raml.v2.internal.utils.StreamUtils;
import org.raml.yagi.framework.grammar.rule.ErrorNodeFactory;
import org.raml.yagi.framework.nodes.IncludeErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.StringNode;
import org.raml.yagi.framework.nodes.snakeyaml.NodeParser;
import org.raml.yagi.framework.phase.GrammarPhase;
import org.raml.yagi.framework.phase.Phase;
import org.raml.yagi.framework.phase.TransformationPhase;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.raml.v2.api.model.v10.RamlFragment.Default;
import static org.raml.v2.api.model.v10.RamlFragment.Extension;
import static org.raml.v2.api.model.v10.RamlFragment.Overlay;
import static org.raml.v2.internal.impl.RamlBuilder.FIRST_PHASE;
import static org.raml.v2.internal.impl.RamlBuilder.LIBRARY_LINK_PHASE;
import static org.raml.v2.internal.impl.commons.RamlVersion.RAML_10;

public class Raml10Builder
{

    private final Set<String> openedFiles = new HashSet<>();

    public Node build(String stringContent, RamlFragment fragment, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        return build(null, stringContent, fragment, resourceLoader, resourceLocation, maxPhaseNumber);
    }

    public Node build(Node contextNode, String stringContent, RamlFragment fragment, ResourceLoader resourceLoader, String resourceLocation, int maxPhaseNumber) throws IOException
    {
        if (openedFiles.contains(resourceLocation))
        {

            return new IncludeErrorNode("Cyclic dependency loading file: " + resourceLocation);
        }

        try
        {
            openedFiles.add(resourceLocation);
            Node rootNode = NodeParser.parse(resourceLoader, resourceLocation, stringContent);
            if (rootNode == null)
            {
                return ErrorNodeFactory.createEmptyDocument();
            }
            if (contextNode != null)
                rootNode.setContextNode(contextNode);
            boolean applyExtension = false;
            if ((fragment == Extension || fragment == Overlay) && maxPhaseNumber > FIRST_PHASE)
            {
                applyExtension = true;
                maxPhaseNumber = LIBRARY_LINK_PHASE;
            }
            final List<Phase> phases = createPhases(resourceLoader, fragment);
            rootNode = runPhases(rootNode, phases, maxPhaseNumber);
            if (applyExtension && !RamlNodeUtils.isErrorResult(rootNode))
            {
                rootNode = applyExtension(rootNode, resourceLoader, resourceLocation, fragment);
            }
            rootNode.annotate(new RamlVersionAnnotation(RAML_10));
            return rootNode;
        }
        finally
        {
            openedFiles.remove(resourceLocation);
        }
    }

    private Node runPhases(Node rootNode, List<Phase> phases, int maxPhaseNumber)
    {
        for (int i = 0; i < phases.size(); i++)
        {
            if (i < maxPhaseNumber)
            {
                Phase phase = phases.get(i);
                rootNode = phase.apply(rootNode);

                checkDumpPhases(i, phase, rootNode);
                if (RamlNodeUtils.isErrorResult(rootNode))
                {
                    break;
                }
            }
        }
        return rootNode;
    }

    private void checkDumpPhases(int i, Phase phase, Node rootNode)
    {
        if (Boolean.getBoolean("dump.phases"))
        {
            String dump = new RamlTreeNodeDumper().dump(rootNode);
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
        Node baseNode = new RamlBuilder().build(baseContent, resourceLoader, baseLocation);

        if (!RamlNodeUtils.isErrorResult(baseNode))
        {
            new ExtensionsMerger(fragment == Overlay).merge(baseNode, extensionNode);
            if (!RamlNodeUtils.isErrorResult(baseNode))
            {
                // run all phases on merged document
                List<Phase> phases = createPhases(resourceLoader, Default);
                baseNode = runPhases(baseNode, phases, Integer.MAX_VALUE);
            }
        }
        return baseNode;
    }

    private List<Phase> createPhases(ResourceLoader resourceLoader, RamlFragment fragment)
    {
        // The first phase expands the includes.
        final TransformationPhase includePhase = new TransformationPhase(new IncludeResolver(resourceLoader), new StringTemplateExpressionTransformer());

        final TransformationPhase ramlFragmentsValidator = new TransformationPhase(new RamlFragmentGrammarTransformer());

        final TransformationPhase ramlFragmentsLibraryLinker = new TransformationPhase(new RamlFragmentLibraryLinkingTransformer(this, resourceLoader));

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final GrammarPhase grammarPhase = new GrammarPhase(RamlHeader.getFragmentRule(fragment));

        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // sugar
        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???
        final TransformationPhase libraryLink = new TransformationPhase(new LibraryLinkingTransformation(this, resourceLoader));

        final TransformationPhase referenceCheck = new TransformationPhase(new ReferenceResolverTransformer());

        // Applies resourceTypes and Traits Library
        final TransformationPhase resourcePhase = new TransformationPhase(new ResourceTypesTraitsTransformer(new Raml10Grammar()));

        final TransformationPhase duplicatedPaths = new TransformationPhase(new DuplicatedPathsTransformer());

        // Check unused uri parameters
        final TransformationPhase checkUnusedParameters = new TransformationPhase(new UnusedParametersTransformer());

        // Run grammar again to re-validate tree

        final AnnotationValidationPhase annotationValidationPhase = new AnnotationValidationPhase(resourceLoader);

        final MediaTypeInjectionPhase mediaTypeInjection = new MediaTypeInjectionPhase();

        // Schema Types example validation
        final TransformationPhase schemaValidationPhase = new TransformationPhase(new SchemaValidationTransformer(resourceLoader));

        // Checks types consistency and custom facets
        final TypeValidationPhase typeValidationPhase = new TypeValidationPhase();

        final ExampleValidationPhase exampleValidationPhase = new ExampleValidationPhase(resourceLoader);

        return Arrays.asList(includePhase,
                ramlFragmentsValidator,
                ramlFragmentsLibraryLinker,
                grammarPhase,
                libraryLink,
                referenceCheck,
                resourcePhase,
                duplicatedPaths,
                checkUnusedParameters,
                annotationValidationPhase,
                mediaTypeInjection,
                grammarPhase,
                schemaValidationPhase,
                typeValidationPhase,
                exampleValidationPhase);

    }
}
