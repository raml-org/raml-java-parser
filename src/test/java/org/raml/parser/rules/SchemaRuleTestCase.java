package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.rule.ValidationResult;

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
    public void invalidJsonSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema"));
    }

    @Test
    public void invalidJsonSchemaGlobal()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-json-global.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid JSON schema"));
    }

    @Test
    public void validXmlSchema()
    {
        validateRamlNoErrors("org/raml/schema/valid-xml.yaml");
    }

    @Test
    public void invalidXmlSchema()
    {
        List<ValidationResult> validationResults = validateRaml("org/raml/schema/invalid-xml.yaml");
        assertThat(validationResults.size(), is(1));
        assertThat(validationResults.get(0).getMessage(), containsString("invalid XML schema"));
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
    }
}
