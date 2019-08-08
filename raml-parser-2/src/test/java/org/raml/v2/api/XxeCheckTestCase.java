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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.rules.ExpectedException.none;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.raml.v2.api.model.common.ValidationResult;

public class XxeCheckTestCase
{

    static
    {
        System.setProperty("javax.xml.accessExternalDTD", "");

        try
        {
            File desFile = File.createTempFile("temp", ".raml");
            EXISTENT_FILE = desFile.toURI();
            FileUtils.copyFile(new File(XxeCheckTestCase.class.getResource("/simplelogger.properties").getFile()), desFile);
            // FileUtils.copyFile(new File(XxeCheckTestCase.class.getResource("/fun.dtd").getFile()), desFile);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static final String RAML_TEMPLATE = "#%RAML RAML_VERSION\n"
                                                + "title: api1\n"
                                                + "version: \"1.0\"\n"
                                                + "schemas:\n"
                                                + "  schema1: |\n"
                                                + "   <?xml version=\"1.0\" ?>\n"
                                                + "   <!DOCTYPE r [\n"
                                                + "   <!ELEMENT r ANY >\n"
                                                + "   <!ENTITY sp SYSTEM \"FILE_URL\">\n"
                                                + "   ]>\n"
                                                + "   <r>&sp;</r>\n"
                                                + "/test:\n"
                                                + "  displayName: res1\n"
                                                + "  get:\n"
                                                + "  post:\n";
    private static URI EXISTENT_FILE;
    private static final URI UNEXISTENT_FILE = URI.create("file:///some/file/some/nosuchfile");
    private static final String RAML_1_0 = "1.0";
    private static final String RAML_0_8 = "0.8";

    @Rule
    public ExpectedException expectedException = none();

    @Test
    public void disablesAccessToExternalDtdOnRaml08WithUnexistentFile()
    {
        doXeeValidationTest(RAML_0_8, UNEXISTENT_FILE.toString(), UNEXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml08WithExistentFile()
    {
        doXeeValidationTest(RAML_0_8, EXISTENT_FILE.toString(), EXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml10WithUnexistentFile()
    {
        doXeeValidationTest(RAML_1_0, UNEXISTENT_FILE.toString(), UNEXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml10WithExistentFile()
    {
        doXeeValidationTest(RAML_1_0, EXISTENT_FILE.toString(), EXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml08WithUnexistentFileAbsoluteFile()
    {
        doXeeValidationTest(RAML_0_8, UNEXISTENT_FILE.getPath(), UNEXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml08WithExistentFileAbsoluteFile()
    {
        doXeeValidationTest(RAML_0_8, EXISTENT_FILE.getPath(), EXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml10WithUnexistentFileAbsoluteFile()
    {
        doXeeValidationTest(RAML_1_0, UNEXISTENT_FILE.getPath(), UNEXISTENT_FILE.getPath());
    }

    @Test
    public void disablesAccessToExternalDtdOnRaml10WithExistentAbsoluteFile()
    {
        doXeeValidationTest(RAML_1_0, EXISTENT_FILE.getPath(), EXISTENT_FILE.getPath());
    }

    private void doXeeValidationTest(String ramlVersion, String fileUrl, String name)
    {
        final String schema = RAML_TEMPLATE.replace("RAML_VERSION", ramlVersion).replace("FILE_URL", fileUrl);

        final RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(schema, name);
        final ValidationResult validationResult = ramlModelResult.getValidationResults().get(0);

        for (ValidationResult result : ramlModelResult.getValidationResults())
        {
            System.err.println(result.getMessage());
        }
        assertThat(validationResult.getMessage(), containsString("access is not allowed due to restriction set by the accessExternalDTD property"));
    }
}