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
package org.raml.yagi.framework.model;

import javax.annotation.Nullable;

public class PackageModelBinding implements ModelBinding
{
    private String packageName;

    public PackageModelBinding(String packageName)
    {
        this.packageName = packageName;
    }

    @Nullable
    @Override
    public NodeModelFactory binding(Class<?> clazz)
    {
        final String simpleName = clazz.getSimpleName();
        try
        {
            final Class<?> aClass = Class.forName(packageName + "." + simpleName);
            return new ClassNodeModelFactory((Class<? extends NodeModel>) aClass);
        }
        catch (ClassNotFoundException e)
        {
            // No binding available
            return null;
        }
    }
}
