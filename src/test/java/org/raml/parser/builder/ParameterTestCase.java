package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.POST;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class ParameterTestCase
{

    @Test
    public void whenParameterIsYRequiredShouldBeTrue() throws IOException
    {
        Raml raml = loadRaml();
        UriParameter uriParameter = raml.getBaseUriParameters().get("param2");
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
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/params/required-param.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        return ramlSpecBuilder.build(simpleTest);
    }

    @Test
    public void whenParameterHasMultiTypeOrSingleTypeShouldBeAccepted() throws IOException
    {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("org/raml/params/parameter-multi-type.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);

        Map<String,List<FormParameter>> formParameters = raml.getResources().get("/simple").getAction(POST).getBody().get("multipart/form-data").getFormParameters();

        FormParameter uriParameter = formParameters.get("acl").get(0);
        Assert.assertThat(uriParameter.getType(), CoreMatchers.is(ParamType.STRING));

        List<FormParameter> file = formParameters.get("file");
        Assert.assertThat(file.size(), CoreMatchers.is(2));

        uriParameter = file.get(0);
        Assert.assertThat(uriParameter.getType(), CoreMatchers.is(ParamType.STRING));

        uriParameter = file.get(1);
        Assert.assertThat(uriParameter.getType(), CoreMatchers.is(ParamType.FILE));
    }

}
