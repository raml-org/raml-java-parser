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
package org.raml.parser.visitor;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.lang.StringUtils;
import org.raml.parser.XsdResourceResolver;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.tagresolver.ContextPath;

public class SchemaCompiler
{

    private final static SchemaCompiler instance = new SchemaCompiler();
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

    public Map<String, Object> compile(Map<String, String> schemas)
    {
        Map<String, Object> compiledSchemas = new HashMap<String, Object>();
        for (Map.Entry<String, String> schema : schemas.entrySet())
        {
            Schema compiledSchema = compile(schema.getValue());
            if (compiledSchema != null)
            {
                compiledSchemas.put(schema.getKey(), compiledSchema);
            }
        }
        return compiledSchemas;
    }

    public Schema compile(String schema)
    {
        Schema compiledSchema = null;
        String trimmedSchema = StringUtils.trimToEmpty(schema);
        if (trimmedSchema.startsWith("<") && trimmedSchema.endsWith(">"))
        {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            factory.setResourceResolver(new XsdResourceResolver(contextPath, resourceLoader));
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
}
