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
package org.raml.yagi.framework.suggester;

public class DefaultSuggestion implements Suggestion, Comparable<Suggestion>
{

    public static final String RAML_1_0_HEADER = "RAML 1.0 Header";

    private String label;
    private String description;
    private String value;
    private String prefix;

    public DefaultSuggestion(String value, String description, String label)
    {
        this(value, description, label, "");
    }

    public DefaultSuggestion(String value, String description, String label, String prefix)
    {
        this.value = value;
        this.description = description;
        this.label = label;
        this.prefix = prefix;
    }

    public String getPrefix()
    {
        return prefix;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getDescription()
    {
        return description;
    }

    @Override
    public String getValue()
    {
        return value;
    }

    @Override
    public Suggestion withDescription(String description)
    {
        return new DefaultSuggestion(getValue(), description, getLabel());
    }

    @Override
    public Suggestion withValue(String value)
    {
        return new DefaultSuggestion(value, getDescription(), getLabel());
    }

    @Override
    public Suggestion withPrefix(String prefix)
    {
        return new DefaultSuggestion(getValue(), getDescription(), getLabel(), prefix);
    }

    @Override
    public String toString()
    {
        return "DefaultSuggestion{" +
               "label='" + label + '\'' +
               ", description='" + description + '\'' +
               ", value='" + value + '\'' +
               '}';
    }

    @Override
    public int compareTo(Suggestion other)
    {
        if (RAML_1_0_HEADER.equals(label))
        {
            return -1;
        }

        if (RAML_1_0_HEADER.equals(other.getLabel()))
        {
            return 1;
        }

        return this.getLabel().compareTo(other.getLabel());
    }
}
