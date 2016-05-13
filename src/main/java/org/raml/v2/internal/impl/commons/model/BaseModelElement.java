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

import java.lang.reflect.*;

import org.raml.v2.internal.framework.nodes.Node;
import org.raml.v2.internal.impl.commons.model.builder.ModelUtils;
import org.raml.v2.internal.utils.NodeSelector;

public abstract class BaseModelElement
{

    public abstract Node getNode();

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
