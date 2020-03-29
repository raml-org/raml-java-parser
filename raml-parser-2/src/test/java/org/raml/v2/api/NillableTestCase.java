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
package org.raml.v2.api;

import org.junit.After;
import org.junit.Test;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;
import org.raml.yagi.framework.grammar.rule.BooleanTypeRule;
import org.raml.yagi.framework.util.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.raml.v2.internal.impl.commons.rule.JsonSchemaValidationRule.JSON_SCHEMA_FAIL_ON_WARNING_KEY;

public class NillableTestCase
{
    private String failOnWarningOldValue;

    @After
    public void clearProperties()
    {
        ExampleValidationPhase.NILLABLE_TYPES = false;
        // DateUtils.setFormatters();
    }


    @Test
    public void testNillableTypes()
    {
        ExampleValidationPhase.NILLABLE_TYPES = true;

        RamlModelResult result = getApi(new InputStreamReader(this.getClass().getResourceAsStream("bad.raml")));
        assertFalse(result.hasErrors());
    }

    private RamlModelResult getApi(Reader reader)
    {
        return new RamlModelBuilder().buildApi(reader, ".");
    }
}
