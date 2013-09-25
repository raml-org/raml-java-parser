package org.raml.parser.rules;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class ResourceTypeValidationTestCase
{

    @Test
    public void noParentResourceType() throws Exception
    {
        String raml = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/parser/rules/resource-type-invalid.yaml"));
        List<ValidationResult> errors = RamlValidationService.createDefault().validate(raml);
        assertThat(errors.size(), is(1));
        assertThat(errors.get(0).getMessage(), containsString("resource type not defined: base"));
    }

}
