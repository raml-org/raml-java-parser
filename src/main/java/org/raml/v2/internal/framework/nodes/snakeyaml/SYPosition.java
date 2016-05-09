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
package org.raml.v2.internal.framework.nodes.snakeyaml;

import org.raml.v2.internal.framework.nodes.AbstractPosition;
import org.yaml.snakeyaml.error.Mark;

public class SYPosition extends AbstractPosition
{

    private Mark mark;

    public SYPosition(Mark mark)
    {
        this.mark = mark;
    }

    @Override
    public int getIndex()
    {
        return mark.getIndex();
    }

    @Override
    public int getLine()
    {
        return mark.getLine();
    }

    @Override
    public int getColumn()
    {
        return mark.getColumn();
    }

    @Override
    public String getResource()
    {
        // TODO add the resource where this position belongs too
        return null;
    }

}
