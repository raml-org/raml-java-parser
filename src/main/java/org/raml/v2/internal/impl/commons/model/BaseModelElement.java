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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import org.raml.v2.internal.framework.nodes.ArrayNode;
import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.framework.nodes.SimpleTypeNode;
import org.raml.v2.internal.impl.commons.model.builder.ModelUtils;
import org.raml.v2.internal.utils.NodeSelector;

public abstract class BaseModelElement
{

    protected abstract Node getNode();

    protected String getStringValue(String key)
    {
        return ModelUtils.getStringValue(key, getNode());
    }

    protected StringType getStringTypeValue(String key)
    {
        return ModelUtils.getStringTypeValue(key, getNode());
    }

    protected List<String> getStringList(String key)
    {
        List<String> result = new ArrayList<>();
        Node node = NodeSelector.selectFrom(key, getNode());
        if (node != null)
        {
            if (node instanceof SimpleTypeNode)
            {
                // case when using syntactic sugar for single element
                // that does not require to be in a sequence
                result.add(((SimpleTypeNode) node).getLiteralValue());
            }
            else
            {
                for (Node child : node.getChildren())
                {
                    result.add(String.valueOf(child));
                }
            }
        }
        return result;
    }

    protected <T> List<T> getListFromSeq(String key, Class<T> clazz)
    {
        ArrayList<T> resultList = new ArrayList<>();
        Node parent = NodeSelector.selectFrom(key, getNode());
        if (parent != null)
        {
            if (parent instanceof ArrayNode)
            {
                return getList(key, clazz);
            }
            try
            {
                // case when using syntactic sugar for single element
                // that does not require to be in a sequence
                Constructor<T> constructor = clazz.getConstructor(Node.class);
                resultList.add(constructor.newInstance(parent));
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return resultList;
    }

    protected <T> List<T> getList(String key, Class<T> clazz)
    {
        ArrayList<T> resultList = new ArrayList<>();
        Node parent = NodeSelector.selectFrom(key, getNode());
        if (parent != null)
        {
            for (Node child : parent.getChildren())
            {
                try
                {
                    Constructor<T> constructor = clazz.getConstructor(Node.class);
                    resultList.add(constructor.newInstance(child));
                }
                catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
            }
        }
        return resultList;
    }

    protected <T> T getObject(String key, Class<T> clazz)
    {
        Node settings = NodeSelector.selectFrom(key, getNode());
        if (settings != null)
        {
            try
            {
                Constructor<T> constructor = clazz.getConstructor(Node.class);
                return constructor.newInstance(settings.getParent());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

}
