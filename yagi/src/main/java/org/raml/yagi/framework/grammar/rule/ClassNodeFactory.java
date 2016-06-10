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
package org.raml.yagi.framework.grammar.rule;

import org.raml.yagi.framework.nodes.Node;

import javax.annotation.Nonnull;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClassNodeFactory implements NodeFactory
{

    private Class<? extends Node> clazz;

    public ClassNodeFactory(Class<? extends Node> clazz)
    {
        this.clazz = clazz;
    }

    @Override
    public Node create(@Nonnull Node currentNode, Object... args)
    {
        try
        {
            if (args != null)
            {
                Class[] types = new Class[args.length];
                for (int i = 0; i < args.length; i++)
                {
                    Object arg = args[i];
                    types[i] = arg.getClass();
                }
                try
                {
                    final Constructor constructor = clazz.getConstructor(types);
                    return clazz.cast(constructor.newInstance(args));
                }
                catch (NoSuchMethodException ignored)
                {
                    // If no constructor with the arguments try default constructor
                }
                catch (InvocationTargetException e)
                {
                    throw new RuntimeException(e);
                }
            }
            return clazz.newInstance();
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
}
