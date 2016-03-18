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
import static org.raml.model.ActionType.GET;
import static org.raml.model.ParamType.BOOLEAN;
import static org.raml.model.ParamType.DATE;
import static org.raml.model.ParamType.INTEGER;
import static org.raml.model.ParamType.NUMBER;
import static org.raml.model.ParamType.STRING;

import java.math.BigDecimal;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.model.parameter.QueryParameter;
import org.raml.parser.rule.ValidationResult;

public class ParameterTypesTestCase extends AbstractRamlTestCase
{

    private static final String ramlSource = "org/raml/params/param-types.yaml";
    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml(ramlSource);
    }

    @Test
    public void stringType()
    {
        QueryParameter nameParam = getParameter("name");
        assertThat(nameParam.getDisplayName(), is("name name"));
        assertThat(nameParam.getDescription(), is("name description"));
        assertThat(nameParam.getType(), is(STRING));
        assertThat(nameParam.getEnumeration().size(), is(3));
        assertThat(nameParam.getEnumeration().get(0), is("one"));
        assertThat(nameParam.getEnumeration().get(1), is("two"));
        assertThat(nameParam.getEnumeration().get(2), is("three"));
        assertThat(nameParam.getPattern(), is("[a-z]{3,5}"));
        assertThat(nameParam.getMinLength(), is(3));
        assertThat(nameParam.getMaxLength(), is(5));
        assertThat(nameParam.getExample(), is("two"));
        assertThat(nameParam.isRepeat(), is(false));
        assertThat(nameParam.isRequired(), is(false));
        assertThat(nameParam.getDefaultValue(), is("three"));

        assertThat(nameParam.validate("one"), is(true));
        assertThat(nameParam.validate("two"), is(true));
        assertThat(nameParam.validate("three"), is(true));
        assertThat(nameParam.validate("four"), is(false));
    }

    @Test
    public void integerType()
    {
        QueryParameter ageParam = getParameter("age");
        assertThat(ageParam.getDisplayName(), is("age name"));
        assertThat(ageParam.getDescription(), is("age description"));
        assertThat(ageParam.getType(), is(INTEGER));
        assertThat(ageParam.getMinimum(), is(new BigDecimal(0)));
        assertThat(ageParam.getMaximum(), is(new BigDecimal(190)));
        assertThat(ageParam.getExample(), is("2"));
        assertThat(ageParam.isRepeat(), is(false));
        assertThat(ageParam.isRequired(), is(false));
        assertThat(ageParam.getDefaultValue(), is("3"));

        assertThat(ageParam.validate("0"), is(true));
        assertThat(ageParam.validate("5.5"), is(false));
        assertThat(ageParam.validate("185"), is(true));
        assertThat(ageParam.validate("193"), is(false));
    }

    @Test
    public void numberType()
    {
        QueryParameter priceParam = getParameter("price");
        assertThat(priceParam.getDisplayName(), is("price name"));
        assertThat(priceParam.getDescription(), is("price description"));
        assertThat(priceParam.getType(), is(NUMBER));
        assertThat(priceParam.getMinimum(), is(new BigDecimal("0.10")));
        assertThat(priceParam.getMaximum(), is(new BigDecimal("99.90")));
        assertThat(priceParam.getExample(), is("17.30"));
        assertThat(priceParam.isRepeat(), is(false));
        assertThat(priceParam.isRequired(), is(false));
        assertThat(priceParam.getDefaultValue(), is("23.50"));

        assertThat(priceParam.validate("0.15"), is(true));
        assertThat(priceParam.validate("0.05"), is(false));
        assertThat(priceParam.validate("75.57"), is(true));
        assertThat(priceParam.validate("99.92"), is(false));
    }

    @Test
    public void dateType()
    {
        QueryParameter priceParam = getParameter("time");
        assertThat(priceParam.getDisplayName(), is("time name"));
        assertThat(priceParam.getDescription(), is("time description"));
        assertThat(priceParam.getType(), is(DATE));
        assertThat(priceParam.getExample(), is("Sun, 06 Nov 1994 08:49:37 GMT"));
        assertThat(priceParam.isRepeat(), is(false));
        assertThat(priceParam.isRequired(), is(false));
        assertThat(priceParam.getDefaultValue(), is("Mon, 07 Nov 1994 11:23:41 GMT"));

        assertThat(priceParam.validate("Mon, 07 Nov 1994 11:23:41 GMT"), is(true));
        //assertThat(priceParam.validate("Lunes 5 de abril"), is(false));
    }

    @Test
    public void booleanType()
    {
        QueryParameter priceParam = getParameter("alive");
        assertThat(priceParam.getDisplayName(), is("alive name"));
        assertThat(priceParam.getDescription(), is("alive description"));
        assertThat(priceParam.getType(), is(BOOLEAN));
        assertThat(priceParam.getExample(), is("true"));
        assertThat(priceParam.isRepeat(), is(false));
        assertThat(priceParam.isRequired(), is(false));
        assertThat(priceParam.getDefaultValue(), is("false"));

        assertThat(priceParam.validate("true"), is(true));
        assertThat(priceParam.validate("false"), is(true));
        assertThat(priceParam.validate("yes"), is(false));
        assertThat(priceParam.validate("no"), is(false));
    }

    @Test
    public void defaultEnum()
    {
        QueryParameter defaultEnum = getParameter("default-enum");
        assertThat(defaultEnum.validate("one"), is(true));
        assertThat(defaultEnum.validate("two"), is(true));
        assertThat(defaultEnum.validate("three"), is(false));
    }

    @Test
    public void validate()
    {
        List<ValidationResult> errors = validateRaml(ramlSource);
        Assert.assertTrue("Errors must be empty", errors.isEmpty());
    }

    private QueryParameter getParameter(String param)
    {
        return raml.getResources().get("/simple").getAction(GET).getQueryParameters().get(param);
    }
}
