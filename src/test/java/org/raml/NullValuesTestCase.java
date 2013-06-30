package org.raml;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.parser.visitor.YamlDocumentBuilder;

import static junit.framework.Assert.assertNotNull;

public class NullValuesTestCase {

    @Test
    public void nullValues() throws Exception {
        String simpleTest = IOUtils.toString(getClass().getClassLoader().getResourceAsStream(
                "org/raml/null-elements.yaml"));
        YamlDocumentBuilder<Raml> ramlSpecBuilder = new YamlDocumentBuilder<Raml>(Raml.class);
        Raml raml = ramlSpecBuilder.build(simpleTest);
        assertNotNull(raml.getResources().get("/leagues").getAction(ActionType.GET).getResponses().get("200").getBody()
                .get("text/xml"));
        assertNotNull(raml.getResources().get("/leagues").getAction(ActionType.GET).getResponses().get("200").getBody()
                .get("application/json"));

    }
}
