package org.raml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;
import org.raml.parser.annotation.Sequence;

public class ResourceType
{

    @Scalar
    private String name;

    @Mapping(implicit = true)
    private Map<ActionType, Action> actions = new HashMap<ActionType, Action>();

    @Scalar
    private String description;

    @Scalar
    private String summary;

    @Scalar
    private TemplateReference type;

    @Sequence
    private List<TemplateReference> is = new ArrayList<TemplateReference>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public TemplateReference getType()
    {
        return type;
    }

    public void setType(TemplateReference type)
    {
        this.type = type;
    }

    public Map<ActionType, Action> getActions()
    {
        return actions;
    }

    public void setActions(Map<ActionType, Action> actions)
    {
        this.actions = actions;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public List<TemplateReference> getIs()
    {
        return is;
    }

    public void setIs(List<TemplateReference> is)
    {
        this.is = is;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }
}
