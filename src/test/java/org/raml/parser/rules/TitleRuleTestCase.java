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
package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.raml.parser.rule.ValidationMessage.getDuplicateRuleMessage;
import static org.raml.parser.rule.ValidationMessage.getMissingRuleMessage;
import static org.raml.parser.rule.ValidationMessage.getRuleEmptyMessage;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class TitleRuleTestCase extends AbstractRamlTestCase
{

    @Test
    public void testTitleNotEmpty()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title:";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(getRuleEmptyMessage("title")));
    }

    @Test
    public void testTitlePresent()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "version: v28.0\n";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(getMissingRuleMessage("title")));
    }

    @Test
    public void testTitleNotMoreThanOnce()
    {
        String raml = "#%RAML 0.8\n" + "---\n" + "title: bla \n" + "title: bla";
        List<ValidationResult> errors = validateRaml(raml, "");
        assertFalse("Errors must not be empty", errors.isEmpty());
        assertThat(errors.get(0).getMessage(), is(getDuplicateRuleMessage("title")));
    }

}
