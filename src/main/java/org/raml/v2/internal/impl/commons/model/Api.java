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
package org.raml.v2.internal.impl.commons.model;

import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.nodes.RamlDocumentNode;
import org.raml.v2.internal.impl.commons.nodes.ResourceNode;

public class Api extends LibraryBase
{

    private RamlDocumentNode node;

    public Api(RamlDocumentNode delegateNode)
    {
        node = delegateNode;
    }

    @Override
    protected Node getNode()
    {
        return node;
    }

    public String title()
    {
        return getStringValue("title");
    }

    public String version()
    {
        return getStringValue("version");
    }

    public StringType baseUri()
    {
        return getStringTypeValue("baseUri");
    }

    public List<StringType> mediaType()
    {
        return getListFromSeq("mediaType", StringType.class);
    }

    public List<DocumentationItem> documentation()
    {
        return getList("documentation", DocumentationItem.class);
    }

    public List<Resource> resources()
    {
        ArrayList<Resource> resultList = new ArrayList<>();
        for (Node item : node.getChildren())
        {
            if (item instanceof ResourceNode)
            {
                resultList.add(new Resource((ResourceNode) item));
            }
        }
        return resultList;
    }

    public List<String> protocols()
    {
        return getStringList("protocols");
    }

    public List<SecuritySchemeRef> securedBy()
    {
        return getList("securedBy", SecuritySchemeRef.class);
    }

    public List<GlobalSchema> schemasV08()
    {
        return getList("schemas", GlobalSchema.class);
    }

    public List<TypeDeclaration> baseUriParameters()
    {
        return getList("baseUriParameters", TypeDeclaration.class);
    }

    public String ramlVersion()
    {
        return node.getVersion().value();
    }

}
