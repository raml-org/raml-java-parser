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
package org.raml.parser.builder;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.raml.model.Raml;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlDocumentBuilder;
import org.raml.parser.visitor.RamlValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRamlTestCase
{

    protected static final Logger logger = LoggerFactory.getLogger(AbstractRamlTestCase.class);

    protected static Raml parseRaml(String resource)
    {
        return new RamlDocumentBuilder().build(getInputStream(resource));
    }

    protected static Raml parseRaml(String resource, RamlDocumentBuilder builder)
    {
        return builder.build(getInputStream(resource));
    }

    protected static List<ValidationResult> validateRaml(String resource)
    {
        return RamlValidationService.createDefault().validate(getResourceAsString(resource));
    }

    protected static void validateRamlNoErrors(String resource)
    {
        List<ValidationResult> validationResults = validateRaml(resource);
        if (!validationResults.isEmpty())
        {
            StringBuilder msg = new StringBuilder("Unexpected errors:\n ");
            for (ValidationResult vr : validationResults)
            {
                msg.append("\t\t").append(vr.toString()).append("\n");
            }
            logger.error(msg.toString());
        }
        assertTrue("Errors must be empty", validationResults.isEmpty());
    }

    public static String getResourceAsString(String resource)
    {
        try
        {
            return IOUtils.toString(getInputStream(resource));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(String resource)
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }
}
