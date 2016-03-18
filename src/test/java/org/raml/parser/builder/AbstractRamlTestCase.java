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

    protected static Raml parseRaml(String resourceLocation)
    {
        return new RamlDocumentBuilder().build(getInputStream(resourceLocation), resourceLocation);
    }

    protected static Raml parseRaml(String raml, String resourceLocation)
    {
        return new RamlDocumentBuilder().build(raml, resourceLocation);
    }

    protected static Raml parseRaml(String resourceLocation, RamlDocumentBuilder builder)
    {
        return builder.build(getInputStream(resourceLocation), resourceLocation);
    }

    protected static List<ValidationResult> validateRaml(String resourceLocation)
    {
        return RamlValidationService.createDefault().validate(resourceLocation);
    }

    protected static List<ValidationResult> validateRaml(String raml, String resourceLocation)
    {
        return RamlValidationService.createDefault().validate(raml, resourceLocation);
    }

    protected static void validateRamlNoErrors(String resourceLocation)
    {
        List<ValidationResult> validationResults = validateRaml(resourceLocation);
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

    public static String getResourceAsString(String resourceLocation)
    {
        try
        {
            return IOUtils.toString(getInputStream(resourceLocation));
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static InputStream getInputStream(String resourceLocation)
    {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceLocation);
    }
}
