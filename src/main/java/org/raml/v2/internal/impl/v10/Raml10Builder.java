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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.v2.internal.framework.grammar.rule.ErrorNodeFactory;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.phase.*;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;
import org.raml.v2.internal.impl.v10.phase.MediaTypeInjection;
import org.raml.v2.internal.impl.v10.phase.TypesTransformer;
import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.framework.nodes.ErrorNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.StringNode;
import org.raml.v2.internal.framework.nodes.snakeyaml.RamlNodeParser;
import org.raml.v2.internal.framework.phase.GrammarPhase;
import org.raml.v2.internal.framework.phase.Phase;
import org.raml.v2.internal.framework.phase.TransformationPhase;
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
        if (fragment == RamlFragment.Extension && maxPhaseNumber > RamlBuilder.FIRST_PHASE)
        {
            applyExtension = true;
            maxPhaseNumber = RamlBuilder.SUGAR_PHASE;
        }
        final List<Phase> phases = createPhases(resourceLoader, resourceLocation, fragment);
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
        if (applyExtension && rootNode.findDescendantsWith(ErrorNode.class).isEmpty())
        {
            return applyExtension(rootNode, resourceLoader, resourceLocation);
        }
        return rootNode;
    }

    private Node applyExtension(Node extensionNode, ResourceLoader resourceLoader, String resourceLocation) throws IOException
    {
        StringNode baseRef = (StringNode) extensionNode.get("extends");
        RamlBuilder builder = new RamlBuilder(RamlBuilder.SUGAR_PHASE);
        InputStream baseStream = resourceLoader.fetchResource(baseRef.getValue());
        String baseContent = StreamUtils.toString(baseStream);
        Node baseNode = builder.build(baseContent, resourceLoader, resourceLocation);

        if (!baseNode.findDescendantsWith(ErrorNode.class).isEmpty())
        {
            return baseNode;
        }

        if (isOverlayOrExtension(baseContent))
        {
            applyExtension(baseNode, resourceLoader, resourceLocation);
        }

        ExtensionsMerger.merge(baseNode, extensionNode);
        return baseNode;
    }

    private boolean isOverlayOrExtension(String baseContent) throws IOException
    {
        try
        {
            RamlHeader ramlHeader = RamlHeader.parse(baseContent);
            if (ramlHeader.getFragment() == RamlFragment.Extension || ramlHeader.getFragment() == RamlFragment.Overlay)
            {
                return true;
            }
        }
        catch (RamlHeader.InvalidHeaderException e)
        {
            // ignore, detected by the builder
        }
        return false;
    }


    private List<Phase> createPhases(ResourceLoader resourceLoader, String resourceLocation, RamlFragment fragment)
    {
        // The first phase expands the includes.
        final TransformationPhase first = new TransformationPhase(new IncludeResolver(resourceLoader), new StringTemplateExpressionTransformer());

        final TransformationPhase ramlFragmentsValidator = new TransformationPhase(new RamlFragmentGrammarTransformer(resourceLoader));

        // Runs Schema. Applies the Raml rules and changes each node for a more specific. Annotations Library TypeSystem
        final Raml10Grammar raml10Grammar = new Raml10Grammar();

        final GrammarPhase second = new GrammarPhase(fragment.getRule(raml10Grammar));
        // Detect invalid references. Library resourceTypes and Traits. This point the nodes are good enough for Editors.

        // sugar
        final SugarRushPhase sugar = new SugarRushPhase();
        // Normalize resources and detects duplicated ones and more than one use of url parameters. ???

        final TransformationPhase libraryLink = new TransformationPhase(new LibraryLinkingTransformation(resourceLoader));

        final TransformationPhase referenceCheck = new TransformationPhase(new ReferenceResolverTransformer());

        // Applies resourceTypes and Traits Library
        final TransformationPhase third = new TransformationPhase(new ResourceTypesTraitsTransformer(raml10Grammar));

        final Phase typesTransformation = new TransformationPhase(new TypesTransformer(resourceLocation));

        // Run grammar again to re-validate tree
        final Phase thirdAndAHalf = second;

        final AnnotationValidationPhase fourth = new AnnotationValidationPhase();

        final MediaTypeInjection fifth = new MediaTypeInjection();

        // Schema Types example validation

        final SchemaValidationPhase sixth = new SchemaValidationPhase(resourceLoader);

        final ExampleValidationPhase seventh = new ExampleValidationPhase(resourceLoader);

        return Arrays.asList(first, ramlFragmentsValidator, sugar, second, libraryLink, referenceCheck, third, typesTransformation, thirdAndAHalf, fourth, fifth, sixth, seventh);

    }
}
