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

import com.google.gson.stream.JsonWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.raml.v2.api.model.common.ValidationResult;
import org.raml.v2.dataprovider.TestDataProvider;
import org.raml.yagi.framework.model.ModelUtils;

@RunWith(Parameterized.class)
public class ApiModelParserTestCase extends TestDataProvider
{


    public ApiModelParserTestCase(File input, File expectedOutput, String name)
    {
        super(input, expectedOutput, name);
    }

    @Parameterized.Parameters(name = "{2}")
    public static Collection<Object[]> getData() throws URISyntaxException
    {
        return getData(ApiModelParserTestCase.class.getResource("").toURI(), "input.raml", "model.json");
    }

    @Test
    public void basicRaml() throws Exception
    {

        final RamlModelResult ramlModelResult = new RamlModelBuilder().buildApi(input);
        final List<ValidationResult> validationResults = ramlModelResult.getValidationResults();
        Assert.assertTrue("Raml has error " + validationResults.toString(), validationResults.isEmpty());
        final StringWriter out = new StringWriter();
        final JsonWriter jsonWriter = new JsonWriter(out);
        jsonWriter.setIndent(" ");
        dumpApiToJson(ramlModelResult, jsonWriter);
        dump = out.toString();
        expected = IOUtils.toString(new FileInputStream(expectedOutput), "UTF-8");
        Assert.assertTrue(jsonEquals(dump, expected));
    }

    private void dumpApiToJson(RamlModelResult ramlModelResult, JsonWriter jsonWriter) throws Exception
    {
        if (ramlModelResult.isVersion10())
        {
            dumpToJson(org.raml.v2.api.model.v10.api.Api.class, ramlModelResult.getApiV10(), jsonWriter);
        }
        else
        {
            dumpToJson(org.raml.v2.api.model.v08.api.Api.class, ramlModelResult.getApiV08(), jsonWriter);
        }
    }


    private void dumpToJson(Type definitionClass, Object value, JsonWriter jsonWriter) throws IOException, InvocationTargetException, IllegalAccessException
    {

        if (value == null)
        {
            jsonWriter.nullValue();
        }
        else if (value instanceof String)
        {
            jsonWriter.value(value.toString());
        }
        else if (value instanceof Number)
        {
            jsonWriter.value((Number) value);
        }
        else if (value instanceof Boolean)
        {
            jsonWriter.value((Boolean) value);
        }
        else if (value.getClass().isEnum())
        {
            jsonWriter.value(value.toString());
        }
        else if (value instanceof List)
        {
            jsonWriter.beginArray();
            for (java.lang.Object o : ((List) value))
            {
                final Class<?> genericListType;
                if (o != null && o.getClass().getInterfaces().length > 0)
                {
                    genericListType = o.getClass().getInterfaces()[0];
                }
                else
                {
                    genericListType = (Class<?>) ((ParameterizedType) definitionClass).getActualTypeArguments()[0];
                }
                dumpToJson(genericListType, o, jsonWriter);
            }
            jsonWriter.endArray();
        }
        else
        {
            jsonWriter.beginObject();
            final Method[] declaredMethods = ModelUtils.toClass(definitionClass).getMethods();
            Arrays.sort(declaredMethods, new Comparator<Method>()
            {
                @Override
                public int compare(Method o1, Method o2)
                {
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (Method declaredMethod : declaredMethods)
            {
                if (declaredMethod.getParameterTypes().length == 0)
                {
                    if (!isRecursiveMethod(declaredMethod))
                    {
                        final Object methodResult = declaredMethod.invoke(value);
                        jsonWriter.name(declaredMethod.getName());
                        dumpToJson(declaredMethod.getGenericReturnType(), methodResult, jsonWriter);
                    }
                }
            }
            jsonWriter.endObject();
        }

    }

    private boolean isRecursiveMethod(Method declaredMethod)
    {
        return declaredMethod.getName().startsWith("parent")
               ||
               (declaredMethod.getDeclaringClass().getSimpleName().equals("Method") && declaredMethod.getName().equals("resource"));
    }


}
