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
package org.raml.parser.tagresolver;

import static org.raml.parser.tagresolver.IncludeResolver.SEPARATOR;

import java.util.List;

import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.visitor.IncludeInfo;
import org.raml.parser.visitor.NodeHandler;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import com.google.common.collect.Lists;

public class CompoundIncludeResolver implements TagResolver, ContextPathAware
{

    public static final String INCLUDE_COMPOUND_APPLIED_TAG = "!include-compound" + SEPARATOR;
    private ContextPath contextPath;

    public static List<IncludeInfo> unmarshall(Tag tag)
    {
        String compoundIncludeTag = tag.getValue().substring(INCLUDE_COMPOUND_APPLIED_TAG.length());

        int endOfFirstLength = compoundIncludeTag.indexOf(SEPARATOR);
        int firstIncludeLength = Integer.parseInt(compoundIncludeTag.substring(0, endOfFirstLength));
        String firstAppliedInclude = compoundIncludeTag.substring(endOfFirstLength + 1, endOfFirstLength + 1 + firstIncludeLength);

        IncludeInfo firstIncludeInfo = new IncludeInfo(new Tag(firstAppliedInclude));

        String compoundIncludeTag1 = compoundIncludeTag.substring(endOfFirstLength + 1 + firstIncludeLength + 1);

        int endOfSecondLength = compoundIncludeTag1.indexOf(SEPARATOR);
        int secondIncludeLength = Integer.parseInt(compoundIncludeTag1.substring(0, endOfSecondLength));
        String secondAppliedInclude = compoundIncludeTag1.substring(endOfSecondLength + 1, endOfSecondLength + 1 + secondIncludeLength);

        IncludeInfo secondIncludeInfo = new IncludeInfo(new Tag(secondAppliedInclude));

        return Lists.newArrayList(firstIncludeInfo, secondIncludeInfo);
    }

    @Override
    public void setContextPath(ContextPath contextPath)
    {
        this.contextPath = contextPath;
    }

    @Override
    public ContextPath getContextPath()
    {
        return contextPath;
    }

    @Override
    public boolean handles(Tag tag)
    {
        return tag.startsWith(INCLUDE_COMPOUND_APPLIED_TAG);
    }

    @Override
    public Node resolve(Node valueNode, ResourceLoader resourceLoader, NodeHandler nodeHandler)
    {
        return valueNode;
    }

    @Override
    public void beforeProcessingResolvedNode(Tag tag, Node originalNode, Node resolvedNode)
    {
        if (tag.startsWith(INCLUDE_COMPOUND_APPLIED_TAG))
        {
            List<IncludeInfo> includes = unmarshall(tag);
            for (IncludeInfo include : includes)
            {
                contextPath.push(include);
            }
        }
    }

    @Override
    public void afterProcessingResolvedNode(Tag tag, Node originalNode, Node resolvedNode)
    {
        if (tag.startsWith(INCLUDE_COMPOUND_APPLIED_TAG))
        {
            List<IncludeInfo> includes = unmarshall(tag);
            for (IncludeInfo include : includes)
            {
                contextPath.pop();
            }
        }
    }


}
