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

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ExclusiveKeys
{
    protected List<Rule> keys;

    public ExclusiveKeys(@Nonnull List<Rule> keys)
    {
        if (keys.isEmpty())
        {
            throw new IllegalArgumentException("rules cannot be empty");
        }
        this.keys = keys;
    }

    public ExclusiveKeys(Rule... fields)
    {
        this(Arrays.asList(fields));
    }

    public List<Rule> getAllRules()
    {
        return keys;
    }
}
