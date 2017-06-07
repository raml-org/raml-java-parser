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
package org.raml.v2.internal.utils;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.raml.v2.internal.utils.Inflector.lowercamelcase;
import static org.raml.v2.internal.utils.Inflector.lowercase;
import static org.raml.v2.internal.utils.Inflector.lowerhyphencase;
import static org.raml.v2.internal.utils.Inflector.lowerunderscorecase;
import static org.raml.v2.internal.utils.Inflector.pluralize;
import static org.raml.v2.internal.utils.Inflector.singularize;
import static org.raml.v2.internal.utils.Inflector.uppercamelcase;
import static org.raml.v2.internal.utils.Inflector.uppercase;
import static org.raml.v2.internal.utils.Inflector.upperhyphencase;
import static org.raml.v2.internal.utils.Inflector.upperunderscorecase;

import org.junit.Test;

public class InflectorTestCase
{

    @Test
    public void singularizer()
    {
        assertThat(singularize("users"), is("user"));
        assertThat(singularize("user"), is("user"));
        assertThat(singularize("statuses"), is("status"));
        assertThat(singularize("status"), is("status"));
        assertThat(singularize("buses"), is("bus"));
        assertThat(singularize("bus"), is("bus"));
        assertThat(singularize("analysis"), is("analysis"));
        assertThat(singularize("analyses"), is("analysis"));
    }

    @Test
    public void pluralizer()
    {
        assertThat(pluralize("user"), is("users"));
        assertThat(pluralize("users"), is("users"));
        assertThat(pluralize("status"), is("statuses"));
        assertThat(pluralize("statuses"), is("statuses"));
        assertThat(pluralize("bus"), is("buses"));
        assertThat(pluralize("buses"), is("buses"));
        assertThat(pluralize("analysis"), is("analyses"));
        assertThat(pluralize("analyses"), is("analyses"));

    }

    @Test
    public void upperCase()
    {
        assertThat(uppercase("userId"), is("USERID"));
    }

    @Test
    public void lowerCase()
    {
        assertThat(lowercase("userId"), is("userid"));
    }

    @Test
    public void lowerCamelCase()
    {
        assertThat(lowercamelcase("UserId"), is("userId"));
    }

    @Test
    public void upperCamelCase()
    {
        assertThat(uppercamelcase("userId"), is("UserId"));
    }

    @Test
    public void lowerUnderscoreCase()
    {
        assertThat(lowerunderscorecase("userId"), is("user_id"));
    }

    @Test
    public void upperUnderscoreCase()
    {
        assertThat(upperunderscorecase("userId"), is("USER_ID"));
    }

    @Test
    public void lowerHyphenCase()
    {
        assertThat(lowerhyphencase("userId"), is("user-id"));
    }

    @Test
    public void upperHyphenCase()
    {
        assertThat(upperhyphencase("userId"), is("USER-ID"));
    }
}
