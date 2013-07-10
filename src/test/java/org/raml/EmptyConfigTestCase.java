package org.raml;

import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.visitor.YamlDocumentBuilder;

public class EmptyConfigTestCase
{

    @Test
    public void emptyConfigTestCase()
    {
        String simpleTest = "";
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);
        
        Assert.assertTrue(raml != null);
    }

}
