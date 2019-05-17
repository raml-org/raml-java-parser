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
package org.raml.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.builder.GlobalSchemaSequenceTupleBuilder;
import org.raml.parser.builder.ProtocolBuilder;
import org.raml.parser.resolver.ResourceHandler;
import org.raml.parser.rule.ProtocolRule;
import org.raml.parser.rule.SecurityReferenceSequenceRule;


public class Raml implements Serializable
{

    private static final long serialVersionUID = -7107368438040691199L;

    @Scalar(required = true)
    private String title;

    @Scalar()
    private String version;

    @Scalar(rule = org.raml.parser.rule.BaseUriRule.class)
    private String baseUri;

    @Sequence(rule = ProtocolRule.class, builder = ProtocolBuilder.class)
    private List<Protocol> protocols = new ArrayList<Protocol>();

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, UriParameter> baseUriParameters = new LinkedHashMap<String, UriParameter>();

    @Scalar()
    private String mediaType;

    @Sequence(rule = org.raml.parser.rule.GlobalSchemasRule.class, builder = GlobalSchemaSequenceTupleBuilder.class)
    private List<Map<String, String>> schemas = new ArrayList<Map<String, String>>();

    private transient Map<String, Object> compiledSchemas;

    @Sequence
    private List<Map<String, Template>> resourceTypes = new ArrayList<Map<String, Template>>();

    @Sequence
    private List<Map<String, Template>> traits = new ArrayList<Map<String, Template>>();

    @Sequence
    private List<Map<String, SecurityScheme>> securitySchemes = new ArrayList<Map<String, SecurityScheme>>();

    @Sequence(rule = SecurityReferenceSequenceRule.class)
    private List<SecurityReference> securedBy = new ArrayList<SecurityReference>();

    @Mapping(handler = ResourceHandler.class, implicit = true)
    private Map<String, Resource> resources = new LinkedHashMap<String, Resource>();

    @Sequence
    private List<DocumentationItem> documentation;

    public Raml()
    {
    }

    public void setDocumentation(List<DocumentationItem> documentation)
    {
        this.documentation = documentation;
    }

    public List<DocumentationItem> getDocumentation()
    {
        return documentation;
    }

    public void setBaseUriParameters(Map<String, UriParameter> uriParameters)
    {
        this.baseUriParameters = uriParameters;
    }

    public void setResources(Map<String, Resource> resources)
    {
        this.resources = resources;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getBaseUri()
    {
        return baseUri;
    }

    public void setBaseUri(String baseUri)
    {
        this.baseUri = baseUri;
    }

    public String getBasePath()
    {
        //skip protocol separator "//"
        int start = baseUri.indexOf("//") + 2;
        if (start == -1)
        {
            start = 0;
        }

        start = baseUri.indexOf("/", start);
        return baseUri.substring(start);
    }

    public String getUri()
    {
        return "";
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public void setMediaType(String mediaType)
    {
        this.mediaType = mediaType;
    }

    public Map<String, Resource> getResources()
    {
        return resources;
    }

    public Map<String, UriParameter> getBaseUriParameters()
    {
        return baseUriParameters;
    }

    public List<Map<String, Template>> getResourceTypes()
    {
        return resourceTypes;
    }

    public void setResourceTypes(List<Map<String, Template>> resourceTypes)
    {
        this.resourceTypes = resourceTypes;
    }

    public List<Map<String, Template>> getTraits()
    {
        return traits;
    }

    public void setTraits(List<Map<String, Template>> traits)
    {
        this.traits = traits;
    }

    public List<Map<String, String>> getSchemas()
    {
        return schemas;
    }

    public void setSchemas(List<Map<String, String>> schemas)
    {
        this.schemas = schemas;
    }

    public List<Protocol> getProtocols()
    {
        return protocols;
    }

    public void setProtocols(List<Protocol> protocols)
    {
        this.protocols = protocols;
    }

    public List<Map<String, SecurityScheme>> getSecuritySchemes()
    {
        return securitySchemes;
    }

    public void setSecuritySchemes(List<Map<String, SecurityScheme>> securitySchemes)
    {
        this.securitySchemes = securitySchemes;
    }

    public List<SecurityReference> getSecuredBy()
    {
        return securedBy;
    }

    public void setSecuredBy(List<SecurityReference> securedBy)
    {
        this.securedBy = securedBy;
    }

    public Map<String, String> getConsolidatedSchemas()
    {
        Map<String, String> consolidated = new HashMap<String, String>();
        for (Map<String, String> map : getSchemas())
        {
            consolidated.putAll(map);
        }
        return consolidated;
    }

    public Map<String, Object> getCompiledSchemas()
    {
        return compiledSchemas;
    }

    public void setCompiledSchemas(Map<String, Object> compiledSchemas)
    {
        this.compiledSchemas = compiledSchemas;
    }

    public Resource getResource(String path)
    {
        for (Resource resource : resources.values())
        {
            if (path.startsWith(resource.getRelativeUri()))
            {
                if (path.length() == resource.getRelativeUri().length())
                {
                    return resource;
                }
                if (path.charAt(resource.getRelativeUri().length()) == '/')
                {
                    return resource.getResource(path.substring(resource.getRelativeUri().length()));
                }
            }
        }
        return null;
    }
}
