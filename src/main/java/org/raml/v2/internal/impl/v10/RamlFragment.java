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
package org.raml.v2.internal.impl.v10;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.framework.grammar.rule.Rule;
import org.raml.v2.internal.impl.v10.grammar.Raml10Grammar;

import javax.annotation.Nullable;

public enum RamlFragment
{
    DocumentationItem
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.documentation();
        }
    },
    DataType
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.type();
        }
    },
    NamedExample
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return null;
        }
    },
    ResourceType
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.resourceType();
        }
    },
    Trait
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.trait();
        }
    },
    AnnotationTypeDeclaration
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.type();
        }
    },
    Library
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.libraryValue();
        }
    },
    Overlay
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.extension(); // TODO overlay extra validations
        }
    },
    Extension
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.extension();
        }
    },
    Default
    {
        @Override
        public Rule getRule(Raml10Grammar grammar)
        {
            return grammar.raml();
        }
    };


    @Nullable
    public static RamlFragment byName(String name)
    {
        if (StringUtils.isBlank(name))
        {
            return RamlFragment.Default;
        }
        else
        {
            try
            {
                return RamlFragment.valueOf(name);
            }
            catch (IllegalArgumentException iae)
            {
                return null;
            }
        }
    }

    public abstract Rule getRule(Raml10Grammar grammar);
}
