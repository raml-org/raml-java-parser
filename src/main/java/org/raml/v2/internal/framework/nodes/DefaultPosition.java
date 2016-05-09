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
package org.raml.v2.internal.framework.nodes;

public class DefaultPosition extends AbstractPosition
{
    private int index;
    private int line;
    private int column;
    private String resource;

    public DefaultPosition(int index, int line, int column, String resource)
    {
        this.index = index;
        this.line = line;
        this.column = column;
        this.resource = resource;
    }

    @Override
    public int getIndex()
    {
        return index;
    }

    @Override
    public int getLine()
    {
        return line;
    }

    @Override
    public int getColumn()
    {
        return column;
    }

    @Override
    public String getResource()
    {
        return resource;
    }


    public static DefaultPosition emptyPosition()
    {
        return new DefaultPosition(-1, -1, -1, "empty.raml");
    }
}
