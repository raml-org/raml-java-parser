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
package org.raml.parser.rule;

import static org.raml.parser.rule.ValidationResult.UNKNOWN;
import static org.raml.parser.rule.ValidationResult.createErrorResult;
import static org.raml.parser.tagresolver.IncludeResolver.INCLUDE_APPLIED_TAG;
import static org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.cfg.ValidationConfiguration;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.processors.syntax.SyntaxValidator;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.raml.parser.ResolveResourceException;
import org.raml.parser.XsdResourceResolver;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.loader.ResourceLoaderAware;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.tagresolver.ContextPathAware;
import org.raml.parser.utils.NodeUtils;
import org.raml.parser.visitor.IncludeInfo;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class SchemaRule extends SimpleRule implements ContextPathAware, ResourceLoaderAware
{

    private static final SyntaxValidator VALIDATOR = new SyntaxValidator(ValidationConfiguration.newBuilder().setDefaultVersion(SchemaVersion.DRAFTV3).freeze());
    private ContextPath contextPath;
    private ResourceLoader resourceLoader;

    public SchemaRule()
    {
        super("schema", String.class);
    }

    @Override
    public List<ValidationResult> doValidateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = super.doValidateValue(node);

        IncludeInfo globaSchemaIncludeInfo = null;
        ContextPath actualContextPath = contextPath;
        ScalarNode schemaNode = getGlobalSchemaNode(value);
        if (schemaNode == null)
        {
            schemaNode = node;
        }
        else
        {
            value = schemaNode.getValue();
            if (schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG))
            {
                globaSchemaIncludeInfo = new IncludeInfo(schemaNode.getTag());
                actualContextPath = new ContextPath(globaSchemaIncludeInfo);
            }
        }
        if (value == null || NodeUtils.isNonStringTag(schemaNode.getTag()))
        {
            return validationResults;
        }

        String mimeType = ((ScalarNode) getParentTupleRule().getKey()).getValue();
        if (mimeType.contains("json"))
        {
            try
            {
                JsonNode jsonNode = JsonLoader.fromString(value);
                ProcessingReport report = VALIDATOR.validateSchema(jsonNode);
                if (!report.isSuccess())
                {
                    StringBuilder msg = new StringBuilder("invalid JSON schema");
                    msg.append(getSourceErrorDetail(node));
                    for (ProcessingMessage processingMessage : report)
                    {
                        msg.append("\n").append(processingMessage.toString());
                    }
                    validationResults.add(getErrorResult(msg.toString(), getLineOffset(schemaNode), globaSchemaIncludeInfo));
                }
            }
            catch (JsonParseException jpe)
            {
                String msg = "invalid JSON schema" + getSourceErrorDetail(node) + jpe.getOriginalMessage();
                JsonLocation loc = jpe.getLocation();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + loc.getLineNr(), globaSchemaIncludeInfo));
            }
            catch (IOException e)
            {
                String prefix = "invalid JSON schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(prefix + e.getMessage(), UNKNOWN, globaSchemaIncludeInfo));
            }
        }
        else if (mimeType.contains("xml"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new XsdResourceResolver(actualContextPath, resourceLoader));
            try
            {
                factory.newSchema(new StreamSource(new StringReader(value)));
            }
            catch (SAXParseException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node) + e.getMessage();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode) + e.getLineNumber(), globaSchemaIncludeInfo));
            }
            catch (SAXException e)
            {
                String msg = "invalid XML schema" + getSourceErrorDetail(node);
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode), globaSchemaIncludeInfo));
            }
            catch (ResolveResourceException e)
            {
                String msg = "invalid XML schema: " + e.getMessage();
                validationResults.add(getErrorResult(msg, getLineOffset(schemaNode), globaSchemaIncludeInfo));
            }
        }
        return validationResults;
    }

    private ValidationResult getErrorResult(String msg, int line, IncludeInfo globaSchemaIncludeInfo)
    {
        ValidationResult errorResult = createErrorResult(msg, line, UNKNOWN, UNKNOWN);
        if (globaSchemaIncludeInfo != null)
        {
            errorResult.setExtraIncludeInfo(globaSchemaIncludeInfo);
        }
        return errorResult;
    }

    private int getLineOffset(ScalarNode schemaNode)
    {
        boolean isInclude = schemaNode.getTag().startsWith(INCLUDE_APPLIED_TAG);
        return isInclude ? -1 : schemaNode.getStartMark().getLine();
    }

    private String getSourceErrorDetail(ScalarNode node)
    {
        String msg = "";
        if (node instanceof IncludeScalarNode)
        {
            msg = " (" + ((IncludeScalarNode) node).getIncludeName() + ")";
        }
        else if (node.getValue().matches("\\w.*"))
        {
            msg = " (" + node.getValue() + ")";
        }
        return msg + ": ";
    }

    private ScalarNode getGlobalSchemaNode(String key)
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        return schemasRule.getSchema(key);
    }

    @Override
    public TupleRule<?, ?> deepCopy()
    {
        checkClassToCopy(SchemaRule.class);
        SchemaRule copy = new SchemaRule();
        copy.setNodeRuleFactory(getNodeRuleFactory());
        copy.setHandler(getHandler());
        copy.setContextPath(contextPath);
        copy.setResourceLoader(resourceLoader);
        return copy;
    }

    @Override
    public void setContextPath(ContextPath contextPath)
    {
        this.contextPath = contextPath;
    }

    @Override
    public ContextPath getContextPath()
    {
        return contextPath;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader)
    {
        this.resourceLoader = resourceLoader;
    }

}
