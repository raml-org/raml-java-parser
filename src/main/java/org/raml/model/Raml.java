package org.raml.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.ResourceHandler;


public class Raml
{

    @Scalar(required = true)
    private String title;

    @Scalar()
    private String version;

    @Scalar(rule = org.raml.parser.rule.BaseUriRule.class)
    private String baseUri;

    @Scalar()
    private String mediaType;

    @Mapping(rule = org.raml.parser.rule.UriParametersRule.class)
    private Map<String, List<UriParameter>> uriParameters = new HashMap<String, List<UriParameter>>();

    @Mapping(handler = ResourceHandler.class, implicit = true)
    private Map<String, Resource> resources = new HashMap<String, Resource>();

    @Sequence
    private List<DocumentationItem> documentation;

    @Sequence
    private List<Map<String, Template>> resourceTypes;

    @Sequence
    private List<Map<String, Template>> traits;

    @Sequence
    private List<Map<String, String>> schemas = new ArrayList<Map<String, String>>();


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

    public void setUriParameters(Map<String, List<UriParameter>> uriParameters)
    {
        this.uriParameters = uriParameters;
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
        try
        {
            URL url = new URL(baseUri);
            return url.getPath();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
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

    public Map<String, List<UriParameter>> getUriParameters()
    {
        return uriParameters;
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

    public Map<String, String> getConsolidatedSchemas()
    {
        Map<String, String> consolidated = new HashMap<String, String>();
        for (Map<String, String> map : getSchemas())
        {
            consolidated.putAll(map);
        }
        return consolidated;
    }

    public Resource getResource(String path)
    {
        if (path.startsWith(baseUri))
        {
            path = path.substring(baseUri.length());
        }

        String baseUriPath;
        try
        {
            baseUriPath = new URL(baseUri).getPath();
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e); //cannot happen
        }
        if (path.startsWith(baseUriPath))
        {
            path = path.substring(baseUriPath.length());
        }

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
