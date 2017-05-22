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
import org.raml.v2.internal.impl.commons.nodes.TypeExpressionNode;
import org.raml.v2.internal.impl.commons.type.BaseType;
import org.raml.v2.internal.impl.commons.type.ResolvedCustomFacets;
import org.raml.v2.internal.impl.commons.type.ResolvedType;

public abstract class XmlFacetsCapableType extends BaseType implements ResolvedType
{
    private XmlFacets xmlFacets;

    public XmlFacetsCapableType(String typeName, TypeExpressionNode declarationNode, XmlFacets xmlFacets, ResolvedCustomFacets customFacets)
    {
        super(typeName, declarationNode, customFacets);
        this.xmlFacets = xmlFacets;
    }

    public XmlFacetsCapableType(String typeName, TypeExpressionNode typeNode, ResolvedCustomFacets customFacets)
    {
        super(typeName, typeNode, customFacets);
        this.xmlFacets = new XmlFacets();
    }

    protected XmlFacetsCapableType overwriteFacets(XmlFacetsCapableType on, TypeDeclarationNode from)
    {
        on.setXmlFacets(on.getXmlFacets().overwriteFacets(from));
        super.overwriteFacets(on, from);
        return on;
    }

    protected XmlFacetsCapableType mergeFacets(XmlFacetsCapableType on, ResolvedType with)
    {
        if (with instanceof XmlFacetsCapableType)
        {
            on.setXmlFacets(on.getXmlFacets().mergeFacets(((XmlFacetsCapableType) with).getXmlFacets()));
        }
        return on;
    }

    public XmlFacets getXmlFacets()
    {
        return xmlFacets;
    }

    private void setXmlFacets(XmlFacets xmlFacets)
    {
        this.xmlFacets = xmlFacets;
    }
}
