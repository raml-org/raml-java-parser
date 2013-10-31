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
package org.raml.parser.rule;

import static org.raml.parser.tagresolver.IncludeResolver.IncludeScalarNode;
import static org.yaml.snakeyaml.nodes.Tag.STR;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;

import org.eel.kitchen.jsonschema.util.JsonLoader;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.Tag;

public class SchemaRule extends SimpleRule
{

    public SchemaRule()
    {
        super("schema", String.class);
    }

    @Override
    public List<ValidationResult> validateValue(ScalarNode node)
    {
        String value = node.getValue();
        List<ValidationResult> validationResults = super.validateValue(node);

        String mimeType = ((ScalarNode) getParentTupleRule().getKey()).getValue();
        if (mimeType.contains("json") && STR.equals(node.getTag()))
        {
            try
            {
                value = getGlobalSchemaIfDefined(value);
                if (value != null)
                {
                    JsonLoader.fromString(value);
                }
            }
            catch (IOException e)
            {
                String prefix = "invalid JSON schema" + getSourceErrorDetail(node);
                validationResults.add(ValidationResult.createErrorResult(prefix + e.getMessage(), node.getStartMark(), node.getEndMark()));
            }
        }
        else if (mimeType.contains("xml") && STR.equals(node.getTag()))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            try
            {
                value = getGlobalSchemaIfDefined(value);
                if (value != null)
                {
                    factory.newSchema(new StreamSource(new StringReader(value)));
                }
            }
            catch (SAXException e)
            {
                String prefix = "invalid XML schema" + getSourceErrorDetail(node);
                validationResults.add(ValidationResult.createErrorResult(prefix + e.getMessage(), node.getStartMark(), node.getEndMark()));
            }
        }
        return validationResults;
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

    private String getGlobalSchemaIfDefined(String key)
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        Tag tag = schemasRule.getTags().get(key);
        if (isCustomTag(tag))
        {
            return null;
        }
        String globalSchema = schemasRule.getSchemas().get(key);
        return globalSchema != null ? globalSchema : key;
    }

    private boolean isCustomTag(Tag tag)
    {
        return tag != null && !STR.equals(tag);
    }

    private Map<String, String> getGlobalSchemas()
    {
        GlobalSchemasRule schemasRule = (GlobalSchemasRule) getRootTupleRule().getRuleByFieldName("schemas");
        return schemasRule.getSchemas();
    }
}
