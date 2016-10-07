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
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import static org.raml.v2.internal.impl.commons.rule.JsonSchemaValidationRule.JSON_SCHEMA_FAIL_ON_WARNING_KEY;

public class SystemPropertiesTestCase
{
    private String failOnWarningOldValue;

    @After
    public void clearProperties()
    {
        clearFailOnWarning();
    }

    @Test
    public void failOnWarning() throws IOException
    {
        failOnWarningOldValue = System.setProperty(JSON_SCHEMA_FAIL_ON_WARNING_KEY, "true");
        RamlModelResult ramlModelResult = getApi();
        assertThat(ramlModelResult.hasErrors(), is(true));
        assertThat(ramlModelResult.getValidationResults().size(), is(1));
    }

    @Test
    public void doNotFailOnWarning() throws IOException
    {
        failOnWarningOldValue = System.setProperty(JSON_SCHEMA_FAIL_ON_WARNING_KEY, "false");
        RamlModelResult ramlModelResult = getApi();
        assertThat(ramlModelResult.hasErrors(), is(false));
    }

    private RamlModelResult getApi()
    {
        File ramlFile = new File("src/test/resources/org/raml/v2/api/v10/system-properties/jsonschema-fail-on-warning.raml");
        assertTrue(ramlFile.isFile());
        return new RamlModelBuilder().buildApi(ramlFile);
    }

    private void clearFailOnWarning()
    {
        if (failOnWarningOldValue == null)
        {
            System.clearProperty(JSON_SCHEMA_FAIL_ON_WARNING_KEY);
        }
        else
        {
            System.setProperty(JSON_SCHEMA_FAIL_ON_WARNING_KEY, failOnWarningOldValue);
        }
    }

}
