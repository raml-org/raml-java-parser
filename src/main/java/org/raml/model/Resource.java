package org.raml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.model.parameter.UriParameter;
import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Parent;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;
import org.raml.parser.resolver.ResourceHandler;

public class Resource
{

    @Scalar
    private String name;

    @Parent(property = "uri")
    private String parentUri;

    @Key
    private String relativeUri;

    @Mapping
    private Map<String, UriParameter> uriParameters = new HashMap<String, UriParameter>();

    @Mapping(handler = ResourceHandler.class, implicit = true)
    private Map<String, Resource> resources = new HashMap<String, Resource>();

    @Mapping(implicit = true)
    private Map<ActionType, Action> actions = new HashMap<ActionType, Action>();

    @Scalar
    private TemplateReference type;

    @Sequence
    private List<String> use = new ArrayList<String>();


    public Resource()
    {
    }

    public void setRelativeUri(String relativeUri)
    {
        this.relativeUri = relativeUri;
    }

    public String getParentUri()
    {
        return parentUri;
    }

    public void setParentUri(String parentUri)
    {
        this.parentUri = parentUri;
    }

    public void setUriParameters(Map<String, UriParameter> uriParameters)
    {
        this.uriParameters = uriParameters;
    }

    public Map<ActionType, Action> getActions()
    {
        return actions;
    }

    public void setActions(Map<ActionType, Action> actions)
    {
        this.actions = actions;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public String getRelativeUri()
    {
        return relativeUri;
    }

    public String getUri()
    {
        if (parentUri.endsWith("/"))
        {
            return parentUri + relativeUri.substring(1);
        }
        return parentUri + relativeUri;
    }

    public Action getAction(ActionType name)
    {
        return actions.get(name);
    }

    public Action getAction(String name)
    {
        return actions.get(ActionType.valueOf(name.toUpperCase()));
    }

    public Map<String, Resource> getResources()
    {
        return resources;
    }

    public void setResources(Map<String, Resource> resources)
    {
        this.resources = resources;
    }

    public Map<String, UriParameter> getUriParameters()
    {
        return uriParameters;
    }

    public List<String> getUse()
    {
        return use;
    }

    public void setUse(List<String> use)
    {
        this.use = use;
    }

    public TemplateReference getType()
    {
        return type;
    }

    public void setType(TemplateReference type)
    {
        this.type = type;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof Resource))
        {
            return false;
        }

        Resource resource = (Resource) o;

        return parentUri.equals(resource.parentUri) && relativeUri.equals(resource.relativeUri);

    }

    @Override
    public int hashCode()
    {
        int result = parentUri.hashCode();
        result = 31 * result + relativeUri.hashCode();
        return result;
    }

    @Override
    public String toString()
    {
        return "Resource{" +
               "name='" + name + '\'' +
               ", uri='" + parentUri != null ? getUri() : "-" + '\'' +
               '}';
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
