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
package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.raml.parser.rule.ValidationResult.UNKNOWN;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.tagresolver.ContextPath;
import org.raml.parser.visitor.IncludeInfo;

public class SchemaRuleTestCase extends AbstractRamlTestCase
{

    @Test
    public void validJsonSchema()
    {
        validateRamlNoErrors("org/raml/schema/valid-json.yaml");
    }

    @Test
    public void validJsonSchemaGlobal()
    {
        validateRamlNoErrors("org/raml/schema/valid-json-global.yaml");
    }

    @Test
    public void validJsonSchemaGlobalTemplate()
    {
        validateRamlNoErrors("org/raml/schema/valid-json-global-template.yaml");
    }

    @Test
    public void invalidJsonSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema"));
        assertThat(validationResults.get(0).getLine() + 1, is(7 + 4));
    }

    @Test
    public void invalidJsonSchemaValidJson()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-schema.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), allOf(
                containsString("invalid JSON schema"), containsString("value has incorrect type")));
        assertThat(validationResults.get(0).getLine() + 1, is(7));
    }

    @Test
    public void invalidJsonSchemaGlobal()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-global.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema (league)"));
        assertThat(validationResults.get(0).getLine() + 1, is(4 + 4));
    }

    @Test
    public void invalidJsonSchemaInclude()
    {
        String resource = "org/raml/schema/invalid.json";
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-include.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema (" + resource + ")"));
        assertThat(validationResults.get(0).getLine() + 1, is(4));
        assertThat(validationResults.get(0).getStartColumn(), is(UNKNOWN));
        assertThat(validationResults.get(0).getEndColumn(), is(UNKNOWN));
        ContextPath includeContext = validationResults.get(0).getIncludeContext();
        assertThat(includeContext.size(), is(2));
        IncludeInfo includeInfo = includeContext.pop();
        assertThat(includeInfo.getLine() + 1, is(7));
        assertThat(includeInfo.getStartColumn() + 1, is(25));
        assertThat(includeInfo.getEndColumn() + 1, is(46));
        assertThat(includeInfo.getIncludeName(), is(resource));
    }

    @Test
    public void invalidJsonSchemaGlobalTemplate()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-global-template.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema (put-leagues)"));
        assertThat(validationResults.get(0).getLine() + 1, is(4 + 4));
    }

    @Test
    public void invalidJsonSchemaGlobalInclude()
    {
        String resource = "org/raml/schema/invalid.json";
        String globalSchema = "league";
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-global-include.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema (" + globalSchema + ")"));
        assertThat(validationResults.get(0).getLine() + 1, is(4));
        assertThat(validationResults.get(0).getStartColumn(), is(UNKNOWN));
        assertThat(validationResults.get(0).getEndColumn(), is(UNKNOWN));
        ContextPath includeContext = validationResults.get(0).getIncludeContext();
        assertThat(includeContext.size(), is(2));
        IncludeInfo includeInfo = includeContext.pop();
        assertThat(includeInfo.getLine() + 1, is(4));
        assertThat(includeInfo.getStartColumn() + 1, is(15));
        assertThat(includeInfo.getEndColumn() + 1, is(36));
        assertThat(includeInfo.getIncludeName(), is(resource));
    }

    @Test
    @Ignore //TODO to be fixed for RAML 0.9
    public void invalidJsonSchemaGlobalSequenceInclude()
    {
        String resource = "org/raml/schema/invalid-sequence-include.yaml";
        String globalSchema = "league";
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-global-sequence-include.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema (" + globalSchema + ")"));
        assertThat(validationResults.get(0).getLine() + 1, is(5));
        assertThat(validationResults.get(0).getStartColumn(), is(UNKNOWN));
        assertThat(validationResults.get(0).getEndColumn(), is(UNKNOWN));
        ContextPath includeContext = validationResults.get(0).getIncludeContext();
        assertThat(includeContext.size(), is(2));
        IncludeInfo includeInfo = includeContext.pop();
        assertThat(includeInfo.getLine() + 1, is(4));
        assertThat(includeInfo.getStartColumn() + 1, is(15));
        assertThat(includeInfo.getEndColumn() + 1, is(52));
        assertThat(includeInfo.getIncludeName(), is(resource));
    }

    @Test
    public void validXmlSchema()
    {
        validateRamlNoErrors("org/raml/schema/valid-xml.yaml");
    }

    @Test
    public void validUtf8IncludeUtf16XmlSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/valid-xml-utf8-include-utf16.yaml");
        assertThat(validationResults.size(), is(0));
    }

    @Test
    public void invalidXmlSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-xml.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid XML schema"));
        assertThat(validationResults.get(0).getLine() + 1, is(7 + 8));
    }

    @Test
    public void validXmlSchemaGlobal()
    {
        validateRamlNoErrors("org/raml/schema/valid-xml-global.yaml");
    }

    @Test
    public void invalidXmlSchemaGlobal()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-xml-global.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid XML schema"));
        assertThat(validationResults.get(0).getLine() + 1, is(4 + 8));
    }


    @Test
    public void invalidXmlSchemaInclude()
    {
        String resource = "org/raml/schema/invalid.xsd";
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-xml-include.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid XML schema (" + resource + ")"));
        assertThat(validationResults.get(0).getLine() + 1, is(8));
        assertThat(validationResults.get(0).getStartColumn(), is(UNKNOWN));
        assertThat(validationResults.get(0).getEndColumn(), is(UNKNOWN));
        ContextPath includeContext = validationResults.get(0).getIncludeContext();
        assertThat(includeContext.size(), is(2));
        IncludeInfo includeInfo = includeContext.pop();
        assertThat(includeInfo.getLine() + 1, is(7));
        assertThat(includeInfo.getStartColumn() + 1, is(25));
        assertThat(includeInfo.getEndColumn() + 1, is(45));
        assertThat(includeInfo.getIncludeName(), is(resource));
    }

    @Test
    public void validXsdInclude()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/xsd-includer.raml");
        assertThat(validationResults.size(), is(0));
    }

    @Test
    public void validGlobalXsdInclude()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/xsd-global-includer.raml");
        assertThat(validationResults.size(), is(0));
    }


    @Test
    public void validJsonSchemaRef()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/json-schema-ref.raml");
        assertThat(validationResults.size(), is(0));
    }

    @Test
    public void validGlobalJsonSchemaRef()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/json-schema-global-ref.raml");
        assertThat(validationResults.size(), is(0));
    }

}
