/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.visitor;

import static java.lang.System.currentTimeMillis;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.TagResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.NodeId;

public class YamlValidationService
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private List<ValidationResult> errorMessage;
    private YamlValidator yamlValidator;
    private ResourceLoader resourceLoader;
    private TagResolver[] tagResolvers;

    protected YamlValidationService(ResourceLoader resourceLoader, YamlValidator yamlValidator, TagResolver[] tagResolvers)
    {
        this.resourceLoader = resourceLoader;
        this.yamlValidator = yamlValidator;
        this.errorMessage = new ArrayList<ValidationResult>();
        this.tagResolvers = tagResolvers;
    }

    public List<ValidationResult> validate(String content)
    {
        long startTime = currentTimeMillis();

        Yaml yamlParser = new Yaml();

        try
        {
            NodeVisitor nodeVisitor = new NodeVisitor(yamlValidator, resourceLoader, tagResolvers);
            Node root = yamlParser.compose(new StringReader(content));
            errorMessage.addAll(preValidation((MappingNode) root));
            if (errorMessage.isEmpty() && root.getNodeId() == NodeId.mapping)
            {
                nodeVisitor.visitDocument((MappingNode) root);
            }
        }
        catch (MarkedYAMLException mye)
        {
            errorMessage.add(ValidationResult.createErrorResult(mye.getProblem(), mye.getProblemMark(), null));
        }
        catch (YAMLException ex)
        {
            errorMessage.add(ValidationResult.createErrorResult(ex.getMessage()));
        }

        errorMessage.addAll(yamlValidator.getMessages());

        if (logger.isDebugEnabled())
        {
            logger.debug("validation time: " + (currentTimeMillis() - startTime) + "ms.");
        }

        return errorMessage;
    }

    protected List<ValidationResult> preValidation(MappingNode root)
    {
        //template method
        return new ArrayList<ValidationResult>();
    }

}
