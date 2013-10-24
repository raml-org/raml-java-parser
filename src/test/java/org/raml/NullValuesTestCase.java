package org.raml;

import static junit.framework.Assert.assertNotNull;

import java.util.Map;

import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.MimeType;
import org.raml.model.Raml;
import org.raml.parser.builder.AbstractRamlTestCase;

public class NullValuesTestCase extends AbstractRamlTestCase
{

    @Test
    public void nullValues() throws Exception
    {
        Raml raml = parseRaml("org/raml/null-elements.yaml");
        Map<String, MimeType> body = raml.getResources().get("/leagues").getAction(ActionType.GET).getResponses().get("200").getBody();
        assertNotNull(body.get("text/xml"));
        assertNotNull(body.get("application/json"));
    }
}
