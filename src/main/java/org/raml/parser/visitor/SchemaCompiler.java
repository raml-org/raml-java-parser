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

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.XsdResourceResolver;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.tagresolver.IncludeResolver;
import org.yaml.snakeyaml.nodes.ScalarNode;

public final class SchemaCompiler
{

    private static final String SEPARATOR = "-|_";
    private static final SchemaCompiler instance = new SchemaCompiler();
    private ContextPath contextPath;
    private ResourceLoader resourceLoader;

    private SchemaCompiler()
    {
    }

    public static SchemaCompiler getInstance()
    {
        return instance;
    }

    public void init(ContextPath contextPath, ResourceLoader resourceLoader)
    {
        if (contextPath == null || resourceLoader == null)
        {
            throw new IllegalArgumentException("Neither contextPath nor resourceLoader can be null");
        }
        this.contextPath = contextPath;
        this.resourceLoader = resourceLoader;
    }

    public Map<String, Object> compile(Map<String, String> encodedSchemas)
    {
        Map<String, Object> compiledSchemas = new HashMap<String, Object>();
        for (Map.Entry<String, String> encodedSchema : encodedSchemas.entrySet())
        {
            String[] pathAndSchema = decodeIncludePath(encodedSchema.getValue());
            Schema compiledSchema = compile(pathAndSchema[1], pathAndSchema[0]);
            if (compiledSchema != null)
            {
                compiledSchemas.put(encodedSchema.getKey(), compiledSchema);
            }
            else if (StringUtils.isNotBlank(pathAndSchema[0]))
            {
                compiledSchemas.put(encodedSchema.getKey(), pathAndSchema[0]);
            }
            encodedSchema.setValue(pathAndSchema[1]);
        }
        return compiledSchemas;
    }

    public Schema compile(String schema, String path)
    {
        Schema compiledSchema = null;
        String trimmedSchema = StringUtils.trimToEmpty(schema);
        if (trimmedSchema.startsWith("<") && trimmedSchema.endsWith(">"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ContextPath actualContextPath = contextPath;
            if (path != null)
            {
                actualContextPath = new ContextPath(new IncludeInfo(path));
            }
            factory.setResourceResolver(new XsdResourceResolver(actualContextPath, resourceLoader));
            try
            {
                compiledSchema = factory.newSchema(new StreamSource(new StringReader(trimmedSchema)));
            }
            catch (Exception e)
            {
                //ignore exception as the error is detected by the validator
                // and here we cannot tell if the schema is intended for xml or not
            }
        }
        return compiledSchema;
    }

    public Schema compile(String schema)
    {
        return compile(schema, null);
    }

    public static String encodeIncludePath(ScalarNode node)
    {
        String schema = node.getValue();
        String includePath = "";
        if (node instanceof IncludeResolver.IncludeScalarNode)
        {
            includePath = ((IncludeResolver.IncludeScalarNode) node).getIncludeName();
        }
        String includeEncoded = DatatypeConverter.printBase64Binary(includePath.getBytes());

        return includeEncoded + SEPARATOR + schema;
    }

    public static String[] decodeIncludePath(String encodedSchema)
    {
        int idx = encodedSchema.indexOf(SEPARATOR);
        if (idx == -1)
        {
            throw new IllegalArgumentException("Invalid include encoded schema.");
        }
        String base64Path = encodedSchema.substring(0, idx);
        String includePath = new String(DatatypeConverter.parseBase64Binary(base64Path));
        String schema = encodedSchema.substring(idx + SEPARATOR.length(), encodedSchema.length());
        return new String[] {includePath, schema};
    }
}
