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

import static junit.framework.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.parser.builder.AbstractRamlTestCase;

public class NullValuesTestCase extends AbstractRamlTestCase
{

    @Test
    public void nullValues() throws Exception
    {
        Raml raml = parseRaml("org/raml/null-elements.yaml");
        Map<String, MimeType> body = raml.getResources().get("/leagues").getAction(ActionType.GET).getResponses().get("200").getBody();
        assertNotNull(body.get("text/xml"));
        assertNotNull(body.get("application/json"));
    }
}
