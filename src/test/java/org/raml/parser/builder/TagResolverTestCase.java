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
package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.PUT;

import java.util.List;

import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.NodeHandler;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.visitor.RamlValidationService;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class TagResolverTestCase extends AbstractRamlTestCase
{

    public static final String RAML = "org/raml/tag-resolver.yaml";

    @Test
    public void customResolver()
    {
        RamlDocumentBuilder builder = new RamlDocumentBuilder(new DefaultResourceLoader(), new CustomTagResolver());
        Raml raml = parseRaml(RAML, builder);
        assertThat(raml.getTitle(), is("custom tag resolved"));
        assertThat(raml.getResources().get("/media").getAction(PUT).getBody().get("application/raml").getSchema(), is("custom tag resolved"));
    }

    @Test
    public void include()
    {
        RamlDocumentBuilder builder = new RamlDocumentBuilder(new DefaultResourceLoader(), new CustomTagResolver());
        Raml raml = parseRaml(RAML, builder);
        assertThat(raml.getResources().get("/file").getAction(PUT).getBody().get("application/json").getSchema(), containsString("file-json"));
    }

    @Test
    public void validate()
    {
        List<ValidationResult> validationResults = RamlValidationService.createDefault(new DefaultResourceLoader(), new CustomTagResolver()).validate(getResourceAsString(RAML), RAML);
        assertThat(validationResults.size(), is(0));
    }

    private class CustomTagResolver implements TagResolver
    {
        private final Tag CUSTOM_TAG = new Tag("!custom");

        @Override
        public boolean handles(Tag tag)
        {
            return CUSTOM_TAG.equals(tag);
        }

        @Override
        public Node resolve(Node valueNode, ResourceLoader resourceLoader, NodeHandler nodeHandler)
        {
            return new ScalarNode(Tag.STR, "custom tag resolved", valueNode.getStartMark(), valueNode.getEndMark(), ((ScalarNode) valueNode).getStyle());
        }

        @Override
        public void beforeProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
        {
            //do nothing
        }

        @Override
        public void afterProcessingResolvedNode(Tag tag, Node originalValueNode, Node resolvedNode)
        {
            //do nothing
        }
    }
}
