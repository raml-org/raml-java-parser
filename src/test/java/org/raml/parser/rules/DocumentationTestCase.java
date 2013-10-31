/*
 * Copyright (c) MuleSoft, Inc.
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
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class DocumentationTestCase
{

    @Test
    public void documentation() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertTrue("Errors must be empty: " + errors, errors.isEmpty());
    }

    @Test
    public void missingContent() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation-nocontent.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertThat(1, is(errors.size()));
        assertThat(errors.get(0).getMessage(), containsString("content is missing"));
    }

    @Test
    public void missingTitle() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/documentation-notitle.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertThat(1, is(errors.size()));
        assertThat(errors.get(0).getMessage(), containsString("title is missing"));
    }

}
