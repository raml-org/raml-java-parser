package org.raml.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.raml.parser.annotation.Key;
import org.raml.parser.annotation.Mapping;
import org.raml.parser.annotation.Scalar;

public class Trait
{

    @Key
    private String name;

    @Scalar
    private String description;

    @Mapping
    private Map<String, TraitAction> provides = new HashMap<String, TraitAction>();

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public Map<String, TraitAction> getProvides()
    {
        return provides;
    }

    public void setProvides(Map<String, TraitAction> provides)
    {
        this.provides = provides;
    }

    public void applyToResource(Resource resource, Set<ActionType> finalActions)
    {
        for (TraitAction tAction : provides.values())
        {
            String tActionTypeName = tAction.getType().toUpperCase();
            if (tActionTypeName.endsWith("?"))
            {
                tActionTypeName = tActionTypeName.substring(0, tActionTypeName.length() - 1);
            }
            ActionType actionType = ActionType.valueOf(tActionTypeName);
            if (!finalActions.contains(actionType))
            {
                return;
            }
            Action action = resource.getActions().get(actionType);
            if (action == null)
            {
                resource.getActions().put(actionType, tAction.createAction(resource));
            }
            else
            {
                tAction.updateAction(action);
            }
        }
    }

}
