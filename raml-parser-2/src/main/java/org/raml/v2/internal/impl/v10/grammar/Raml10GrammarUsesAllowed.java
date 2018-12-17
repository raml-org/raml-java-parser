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
package org.raml.v2.internal.impl.v10.grammar;

import org.raml.yagi.framework.grammar.rule.ObjectRule;

public class Raml10GrammarUsesAllowed extends Raml10Grammar
{
    @Override
    public ObjectRule explicitType()
    {
        return super.explicitType().with(0, usesField());
    }

    @Override
    ObjectRule propertyType()
    {
        return super.propertyType().with(0, usesField());
    }

    @Override
    public ObjectRule traitParamsResolved()
    {
        return super.traitParamsResolved().with(usesField());
    }
}
