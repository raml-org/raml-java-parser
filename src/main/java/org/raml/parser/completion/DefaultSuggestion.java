/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml.parser.completion;

public class DefaultSuggestion implements Suggestion
{

    private String label;
    private int indentation;

    public DefaultSuggestion(String label)
    {
        this(label, -1);
    }

    public DefaultSuggestion(String label, int indentation)
    {
        if (label == null)
        {
            throw new IllegalArgumentException("label cannot be null");
        }
        this.label = label;
        this.indentation = indentation;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getDescription()
    {
        return "no description";
    }

    @Override
    public String getValue()
    {
        return getLabel();
    }

    @Override
    public int getIndentation()
    {
        return indentation;
    }

    public void setIndentation(int indentation)
    {
        this.indentation = indentation;
    }

    @Override
    public String toString()
    {
        return "DefaultSuggestion{" +
               "label='" + label + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        DefaultSuggestion that = (DefaultSuggestion) o;

        if (label != null ? !label.equals(that.label) : that.label != null)
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return label != null ? label.hashCode() : 0;
    }

    @Override
    public int compareTo(Suggestion that)
    {
        return this.getLabel().compareTo(that.getLabel());
    }
}
