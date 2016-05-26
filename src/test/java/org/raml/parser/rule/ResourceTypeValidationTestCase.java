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
package org.raml.parser.rule;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

public class ResourceTypeValidationTestCase extends AbstractRamlTestCase
{

    @Test
    public void noParentResourceType() throws Exception
    {
        String location = "org/raml/parser/rules/resource-type-invalid.yaml";
        List<ValidationResult> errors = validateRaml(location);
        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), containsString("resource type not defined: base"));
    }

}
