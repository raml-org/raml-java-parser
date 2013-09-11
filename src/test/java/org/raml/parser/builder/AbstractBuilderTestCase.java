package org.raml.parser.builder;

import java.io.InputStream;

import org.raml.model.Raml;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class AbstractBuilderTestCase
{

    protected Raml parseRaml(String resource)
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resource);
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        return ramlSpecBuilder.build(inputStream);
    }

}
