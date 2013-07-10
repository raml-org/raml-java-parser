package org.raml.parser.builder;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class ParameterTestCase
{

    @Test
    public void whenParameterIsYRequiredShouldBeTrue() throws IOException
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/required-param.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);
        
        UriParameter uriParameter = raml.getUriParameters().get("param2");
        Assert.assertThat(uriParameter.isRequired(), CoreMatchers.is(true));
    }

}
