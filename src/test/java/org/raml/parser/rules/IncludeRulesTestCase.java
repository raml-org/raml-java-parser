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
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.visitor.IncludeInfo;

public class IncludeRulesTestCase extends AbstractRamlTestCase
{

    @Test
    public void include()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/parser/rules/includes.yaml");
        assertThat(validationResults.size(), is(0));
    }

    @Test
    public void includeNotFound()
    {
        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includes-bad.yaml");
        assertThat("Errors are not 1 " + errors, errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), is("Include cannot be resolved org/raml/parser/rules/title2.txt"));
    }

    @Test
    public void includeWithError()
    {
        String includedResource1 = "org/raml/parser/rules/included-with-error.yaml";
        String includedResource2 = "org/raml/parser/rules/included-with-error-2.yaml";

        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includes-yaml-with-error.yaml");
        assertThat(errors.size(), is(3));

        assertThat(errors.get(0).getMessage(), containsString("Unknown key: invalidKeyRoot"));
        assertThat(errors.get(0).getIncludeName(), nullValue());
        assertThat(errors.get(0).getLine() + 1, is(6));
        assertThat(errors.get(0).getStartColumn() + 1, is(1));
        assertThat(errors.get(0).getEndColumn() + 1, is(15));

        assertThat(errors.get(1).getMessage(), containsString("Unknown key: invalidKey1"));
        assertThat(errors.get(1).getIncludeName(), is(includedResource1));
        assertThat(errors.get(1).getLine() + 1, is(2));
        assertThat(errors.get(1).getStartColumn() + 1, is(1));
        assertThat(errors.get(1).getEndColumn() + 1, is(12));
        ContextPath contextPath = errors.get(1).getIncludeContext();
        assertThat(contextPath.size(), is(2));
        IncludeInfo includeInfo = contextPath.pop();
        assertThat(includeInfo.getLine() + 1, is(7));
        assertThat(includeInfo.getStartColumn() + 1, is(14));
        assertThat(includeInfo.getEndColumn() + 1, is(47));
        assertThat(includeInfo.getIncludeName(), is(includedResource1));
        assertThat(contextPath.size(), is(1));

        assertThat(errors.get(2).getMessage(), containsString("Unknown key: invalidKey2"));
        assertThat(errors.get(2).getIncludeName(), is(includedResource2));
        assertThat(errors.get(2).getLine() + 1, is(3));
        assertThat(errors.get(2).getStartColumn() + 1, is(1));
        assertThat(errors.get(2).getEndColumn() + 1, is(12));
        contextPath = errors.get(2).getIncludeContext();
        assertThat(contextPath.size(), is(3));
        includeInfo = contextPath.pop();
        assertThat(includeInfo.getLine() + 1, is(3));
        assertThat(includeInfo.getStartColumn() + 1, is(6));
        assertThat(includeInfo.getEndColumn() + 1, is(41));
        assertThat(includeInfo.getIncludeName(), is(includedResource2));
        includeInfo = contextPath.pop();
        assertThat(includeInfo.getLine() + 1, is(7));
        assertThat(includeInfo.getStartColumn() + 1, is(14));
        assertThat(includeInfo.getEndColumn() + 1, is(47));
        assertThat(includeInfo.getIncludeName(), is(includedResource1));
        assertThat(contextPath.size(), is(1));
    }

    @Test
    public void includeEmptyYamlFile()
    {
        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includes-empty.yaml");
        assertThat("Errors are not 1 " + errors, errors.size(), is(2));
        assertThat(errors.get(0).getMessage(), is("Include file is empty org/raml/parser/rules/empty.yaml"));
        assertThat(errors.get(1).getMessage(), is("Invalid value type"));
    }

    @Test
    public void includeRelativeParent()
    {
        List<ValidationResult> errors = validateRaml("org/raml/parser/rules/includesRelative.yaml");
        assertThat(errors.size(), is(0));
    }

}
