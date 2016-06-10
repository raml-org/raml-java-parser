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
package org.raml.v2.internal.impl.v10.type;

import org.raml.v2.internal.impl.commons.nodes.TypeDeclarationNode;
import org.raml.yagi.framework.util.NodeSelector;

public class XmlFacets
{
    private Boolean attribute;
    private Boolean wrapped;
    private String name;
    private String namespace;
    private String prefix;

    public XmlFacets()
    {
    }

    public XmlFacets(Boolean attribute, Boolean wrapped, String name, String namespace, String prefix)
    {
        this.attribute = attribute;
        this.wrapped = wrapped;
        this.name = name;
        this.namespace = namespace;
        this.prefix = prefix;
    }


    public XmlFacets copy()
    {
        return new XmlFacets(attribute, wrapped, name, namespace, prefix);
    }


    public XmlFacets overwriteFacets(TypeDeclarationNode from)
    {
        final XmlFacets copy = copy();
        copy.setAttribute(NodeSelector.selectBooleanValue("xml/attribute", from));
        copy.setName(NodeSelector.selectStringValue("xml/name", from));
        copy.setNamespace(NodeSelector.selectStringValue("xml/namespace", from));
        copy.setPrefix(NodeSelector.selectStringValue("xml/prefix", from));
        copy.setWrapped(NodeSelector.selectBooleanValue("xml/wrapped", from));
        return copy;
    }


    public XmlFacets mergeFacets(XmlFacets with)
    {
        final XmlFacets copy = copy();
        copy.setAttribute(with.getAttribute());
        copy.setName(with.getName());
        copy.setNamespace(with.getNamespace());
        copy.setPrefix(with.getPrefix());
        copy.setWrapped(with.getWrapped());
        return copy;
    }

    // Getters and Setters

    public Boolean getAttribute()
    {
        return attribute;
    }

    public Boolean getWrapped()
    {
        return wrapped;
    }

    public String getName()
    {
        return name;
    }

    public String getNamespace()
    {
        return namespace;
    }

    public String getPrefix()
    {
        return prefix;
    }

    private void setAttribute(Boolean attribute)
    {
        if (attribute != null)
        {
            this.attribute = attribute;
        }
    }

    private void setWrapped(Boolean wrapped)
    {
        if (wrapped != null)
        {
            this.wrapped = wrapped;
        }
    }

    private void setName(String name)
    {
        if (name != null)
        {
            this.name = name;
        }
    }

    private void setNamespace(String namespace)
    {
        if (namespace != null)
        {
            this.namespace = namespace;
        }
    }

    private void setPrefix(String prefix)
    {
        if (prefix != null)
        {
            this.prefix = prefix;
        }
    }


}
