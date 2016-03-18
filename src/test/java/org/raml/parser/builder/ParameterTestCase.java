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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.raml.model.ActionType.POST;

import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.ParamType;
import org.raml.model.Raml;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.QueryParameter;
import org.raml.model.parameter.UriParameter;

public class ParameterTestCase extends AbstractRamlTestCase
{

    private String ramlSource = "org/raml/params/required-param.yaml";

    @Test
    public void whenParameterIsYRequiredShouldBeTrue()
    {
        Raml raml = parseRaml(ramlSource);
        UriParameter uriParameter = raml.getBaseUriParameters().get("param2");
        assertThat(uriParameter.isRequired(), is(true));
    }

    @Test
    public void typeFile()
    {
        Raml raml = parseRaml(ramlSource);
        QueryParameter queryParameter = raml.getResources().get("/resource").getAction(ActionType.GET).getQueryParameters().get("param");
        assertThat(queryParameter.getType(), is(ParamType.FILE));
    }

    @Test
    public void whenParameterHasMultiTypeOrSingleTypeShouldBeAccepted()
    {
        Raml raml = parseRaml("org/raml/params/parameter-multi-type.yaml");

        Map<String, List<FormParameter>> formParameters = raml.getResources().get("/simple").getAction(POST).getBody().get("multipart/form-data").getFormParameters();

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
