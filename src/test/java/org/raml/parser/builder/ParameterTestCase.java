package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class ParameterTestCase
{

    @Test
    public void whenParameterIsYRequiredShouldBeTrue() throws IOException
    {
        Raml raml = loadRaml();
        UriParameter uriParameter = raml.getUriParameters().get("param2");
        assertThat(uriParameter.isRequired(), is(true));
    }

    @Test
    public void typeFile() throws Exception
    {
        Raml raml = loadRaml();
        QueryParameter queryParameter = raml.getResources().get("/resource").getAction(ActionType.GET).getQueryParameters().get("param");
        assertThat(queryParameter.getType(), is(ParamType.FILE));
    }

    private Raml loadRaml() throws IOException
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/required-param.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        return ramlSpecBuilder.build(simpleTest);
    }

}
