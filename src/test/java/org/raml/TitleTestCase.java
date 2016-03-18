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
package org.raml;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationMessage;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;


public class TitleTestCase extends AbstractRamlTestCase
{

    @Test
    public void whenTitleIsNotDefinedErrorShouldBeShown() throws IOException
    {
        String location = "org/raml/title-not-defined.yaml";
        List<ValidationResult> errors = validateRaml(location);
        Assert.assertFalse("Errors must not be empty", errors.isEmpty());
        Assert.assertThat(errors.get(0).getMessage(), CoreMatchers.is(ValidationMessage.getMissingRuleMessage("title")));
    }

}
