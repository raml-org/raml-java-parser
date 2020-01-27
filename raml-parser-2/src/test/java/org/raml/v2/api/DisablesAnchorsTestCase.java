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

import static java.lang.Boolean.TRUE;
import static java.lang.System.clearProperty;
import static java.lang.System.setProperty;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.raml.yagi.framework.nodes.snakeyaml.NodeParser.DISABLE_ANCHORS_PROPERTY;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.Test;
import org.raml.v2.internal.impl.RamlBuilder;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.NodeType;
import org.raml.yagi.framework.nodes.snakeyaml.NodeParser;

/**
 * Tests {@link NodeParser} behavior about disabling/enabling anchors definitions.
 */
public class DisablesAnchorsTestCase
{

    private static final String RAML_WITH_ANCHOR = ""
                                                   + "#%RAML 1.0\n"
                                                   + "title: xml body\n"
                                                   + "/top:\n"
                                                   + "    get:\n"
                                                   + "        body: &bodyPost\n"
                                                   + "            application/xml:\n"
                                                   + "                type: User\n"
                                                   + "        responses:\n"
                                                   + "            200:\n"
                                                   + "                body: *bodyPost";

    @Test
    public void disablesAnchors() throws Exception
    {
        final String originalDisabledValue = System.getProperty(DISABLE_ANCHORS_PROPERTY);

        try
        {
            setDisabledAnchors(TRUE.toString());

            final RamlBuilder builder = new RamlBuilder();

            final Node raml = builder.build(RAML_WITH_ANCHOR);

            assertThat(raml, notNullValue());
            assertThat(raml.getType(), equalTo(NodeType.Error));
            assertThat(((ErrorNode) raml).getErrorMessage(), containsString(
                    "Underlying error while parsing YAML syntax: 'Attempt to define anchor 'bodyPost' but anchors are disabled."));
        }
        finally
        {
            setDisabledAnchors(originalDisabledValue);
        }
    }

    @Test
    public void enablesAnchorsByDefault()
    {
        final RamlBuilder builder = new RamlBuilder();

        final Node raml = builder.build(RAML_WITH_ANCHOR);

        assertThat(raml, notNullValue());
        assertThat(raml.getType(), equalTo(NodeType.Object));
    }

    private void setDisabledAnchors(String newValue)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
    {
        if (newValue == null)
        {
            clearProperty(DISABLE_ANCHORS_PROPERTY);
        }
        else
        {
            setProperty(DISABLE_ANCHORS_PROPERTY, newValue);
        }
        Method setAnchorsDisabledMethod = NodeParser.class.getDeclaredMethod("setAnchorsDisabled");
        setAnchorsDisabledMethod.setAccessible(true);
        setAnchorsDisabledMethod.invoke(NodeParser.class);
    }
}
