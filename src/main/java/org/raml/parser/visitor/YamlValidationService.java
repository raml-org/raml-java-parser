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
package org.raml.parser.visitor;

import static java.lang.System.currentTimeMillis;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.yaml.snakeyaml.nodes.NodeId.mapping;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.loader.ResourceNotFoundException;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.TagResolver;
import org.raml.parser.utils.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.MarkedYAMLException;
import org.yaml.snakeyaml.error.YAMLException;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

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

    public List<ValidationResult> validate(MappingNode root, String resourceLocation)
    {
        NodeVisitor nodeVisitor = new NodeVisitor(yamlValidator, resourceLoader, tagResolvers);
        yamlValidator.getContextPath().pushRoot(resourceLocation);
        errorMessage.addAll(preValidation(root));
        nodeVisitor.visitDocument(root);
        return errorMessage;
    }

    public List<ValidationResult> validate(String resourceLocation)
    {
        InputStream resourceStream = resourceLoader.fetchResource(resourceLocation);
        if (resourceStream == null)
        {
            throw new ResourceNotFoundException(resourceLocation);
        }
        return validate(resourceStream, resourceLocation);
    }

    public List<ValidationResult> validate(String content, String resourceLocation)
    {
        return validate(new StringReader(content), resourceLocation);
    }

    public List<ValidationResult> validate(InputStream content, String resourceLocation)
    {
        return validate(StreamUtils.reader(content), resourceLocation);
    }

    public List<ValidationResult> validate(Reader content, String resourceLocation)
    {
        long startTime = currentTimeMillis();

        try
        {
            Yaml yamlParser = new Yaml();
            Node root = yamlParser.compose(content);
            if (root != null && root.getNodeId() == mapping)
            {
                validate((MappingNode) root, resourceLocation);
            }
            else
            {
                errorMessage.add(createErrorResult("Invalid RAML"));
            }
        }
        catch (MarkedYAMLException mye)
        {
            errorMessage.add(createErrorResult(mye.getProblem(), mye.getProblemMark(), mye.getProblemMark()));
        }
        catch (YAMLException ex)
        {
            errorMessage.add(createErrorResult(ex.getMessage()));
        }
        finally
        {
            IOUtils.closeQuietly(content);
        }

        errorMessage.addAll(yamlValidator.getMessages());

        if (logger.isDebugEnabled())
        {
            logger.debug("validation time: " + (currentTimeMillis() - startTime) + "ms.");
        }

        return errorMessage;
    }

    @Deprecated
    public List<ValidationResult> validate(Reader content)
    {
        return validate(content, new File("").getPath());
    }

    @Deprecated
    public List<ValidationResult> validate(InputStream content)
    {
        return validate(StreamUtils.reader(content));
    }

    protected List<ValidationResult> preValidation(MappingNode root)
    {
        //template method
        return new ArrayList<ValidationResult>();
    }

    protected YamlValidator getValidator()
    {
        return yamlValidator;
    }

}
