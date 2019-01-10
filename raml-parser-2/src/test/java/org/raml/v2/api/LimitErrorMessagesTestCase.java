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

import org.junit.Assert;
import org.junit.Test;
import org.raml.v2.internal.impl.v10.phase.ExampleValidationPhase;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class LimitErrorMessagesTestCase
{
    private static final String LIMIT_ERROR_MESSAGE_LENGTH = "raml.limit_error_message_length";

    @Test
    public void originalErrorMessage()
    {
        ExampleValidationPhase.limitErrorMessageLength = false;
        File ramlFile = new File("src/test/resources/org/raml/v2/api/v10/limit-error-message/limit-error-message.raml");
        assertTrue(ramlFile.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFile);
        Assert.assertEquals(32765, ramlModelResult.getValidationResults().get(0).getMessage().length());
    }

    @Test
    public void limitErrorMessage()
    {
        ExampleValidationPhase.limitErrorMessageLength = true;
        File ramlFile = new File("src/test/resources/org/raml/v2/api/v10/limit-error-message/limit-error-message.raml");
        assertTrue(ramlFile.isFile());
        RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(ramlFile);
        Assert.assertEquals(10042, ramlModelResult.getValidationResults().get(0).getMessage().length());
    }
}