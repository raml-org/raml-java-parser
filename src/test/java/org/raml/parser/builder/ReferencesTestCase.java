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

import org.junit.Ignore;
import org.junit.Test;
import org.raml.parser.loader.ResourceNotFoundException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Ignore
public class ReferencesTestCase extends AbstractRamlTestCase
{

    @Test(expected = ResourceNotFoundException.class)
    public void excessOfReferences()
    {
        parseRaml("org/raml/references/simple.yaml");
    }

    @Test(expected = ResourceNotFoundException.class)
    public void excessWithInclude()
    {
        parseRaml("org/raml/references/includes.yaml");
    }

}
