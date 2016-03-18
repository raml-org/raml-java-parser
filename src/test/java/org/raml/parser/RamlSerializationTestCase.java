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
package org.raml.parser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.SerializationUtils;
import org.junit.Test;
import org.raml.model.Action;
import org.raml.model.ActionType;
import org.raml.model.DocumentationItem;
import org.raml.model.MimeType;
import org.raml.model.Protocol;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.Response;
import org.raml.model.SecurityReference;
import org.raml.model.SecurityScheme;
import org.raml.model.SecuritySchemeDescriptor;
import org.raml.model.SecuritySettings;
import org.raml.model.Template;
import org.raml.model.parameter.FormParameter;
import org.raml.model.parameter.Header;
import org.raml.model.parameter.UriParameter;

public class RamlSerializationTestCase
{

    @Test
    public void roundtrip()
    {
        MimeType mimeType = new MimeType();
        mimeType.setFormParameters(buildMap(Collections.singletonList(new FormParameter())));

        Action action = new Action();
        action.setBody(buildMap(mimeType));
        action.setResponses(buildMap(new Response()));

        Resource resource = new Resource();
        resource.setActions(buildMap(ActionType.GET, action));

        SecuritySchemeDescriptor describedBy = new SecuritySchemeDescriptor();
        describedBy.setHeaders(buildMap(new Header()));

        SecurityScheme securityScheme = new SecurityScheme();
        securityScheme.setDescribedBy(describedBy);
        securityScheme.setSettings(new SecuritySettings());

        Raml raml = new Raml();
        raml.setTitle("hi");
        raml.setBaseUriParameters(buildMap(new UriParameter()));
        raml.setDocumentation(Collections.singletonList(new DocumentationItem()));
        raml.setResources(buildMap(resource));
        raml.setProtocols(Collections.singletonList(Protocol.HTTP));
        raml.setResourceTypes(Collections.singletonList(buildMap(new Template())));
        raml.setTraits(Collections.singletonList(buildMap(new Template())));
        raml.setSecuritySchemes(Collections.singletonList(buildMap(securityScheme)));
        raml.setSecuredBy(Collections.singletonList(new SecurityReference("ref")));

        byte[] bytes = SerializationUtils.serialize(raml);
        Raml copy = (Raml) SerializationUtils.deserialize(bytes);
        assertThat(copy.getTitle(), is(raml.getTitle()));
    }

    private <V> Map<String, V> buildMap(V value)
    {
        return buildMap("key", value);
    }

    private <K, V> Map<K, V> buildMap(K key, V value)
    {
        Map<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

}
