package org.raml.parser.builder;

import java.io.InputStream;

import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class AbstractBuilderTestCase
{

    protected static Raml parseRaml(String resource)
    {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
        return new RamlDocumentBuilder().build(inputStream);
    }

}
