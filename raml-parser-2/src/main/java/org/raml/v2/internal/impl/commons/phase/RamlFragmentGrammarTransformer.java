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
package org.raml.v2.internal.impl.commons.phase;

import static org.raml.v2.internal.utils.PhaseUtils.applyPhases;

import org.raml.v2.api.loader.ResourceLoader;
import org.raml.v2.internal.impl.v10.Raml10Builder;
import org.raml.yagi.framework.grammar.rule.Rule;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.phase.TransformationPhase;
import org.raml.yagi.framework.phase.Transformer;
import org.raml.v2.internal.impl.commons.RamlHeader;
import org.raml.v2.internal.impl.commons.nodes.RamlTypedFragmentNode;
import org.raml.v2.internal.impl.v10.phase.LibraryLinkingTransformation;

public class RamlFragmentGrammarTransformer implements Transformer
{

    private final Raml10Builder builder;
    private final ResourceLoader resourceLoader;

    public RamlFragmentGrammarTransformer(Raml10Builder builder, ResourceLoader resourceLoader)
    {
        this.builder = builder;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public boolean matches(Node node)
    {
        return node instanceof RamlTypedFragmentNode;
    }

    @Override
    public Node transform(Node node)
    {
        final RamlTypedFragmentNode ramlTypedFragmentNode = (RamlTypedFragmentNode) node;
        final Rule rule = RamlHeader.getFragmentRule(ramlTypedFragmentNode.getFragment());
        node = rule.apply(node);
        final Node apply = applyPhases(node, new TransformationPhase(new LibraryLinkingTransformation(builder, resourceLoader)));
        if (apply instanceof RamlTypedFragmentNode)
        {
            // Hack!!!!
            ((RamlTypedFragmentNode) apply).resolveLibraryReference();
        }
        return apply;
    }
}
