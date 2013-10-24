package org.raml;

import org.junit.Assert;
import org.junit.Test;
import org.raml.model.Raml;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class EmptyConfigTestCase
{

    @Test
    public void emptyConfigTestCase()
    {
        String simpleTest = "";
        RamlDocumentBuilder builder = new RamlDocumentBuilder();
        Raml raml = builder.build(simpleTest);
        Assert.assertTrue(raml != null);
    }

}
