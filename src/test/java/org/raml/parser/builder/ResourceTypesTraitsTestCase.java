package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;
import org.raml.model.ActionType;
import org.raml.model.Raml;
import org.raml.model.Resource;
import org.raml.model.parameter.QueryParameter;

public class ResourceTypesTraitsTestCase extends AbstractBuilderTestCase
{

    private static Raml raml;

    @BeforeClass
    public static void init()
    {
        raml = parseRaml("org/raml/types/resource-types-traits.yaml");
    }

    @Test
    public void mixed()
    {
        Resource mixed = raml.getResources().get("/mixed");
        assertThat(mixed.getActions().size(), is(1));
        String[] h = {"hAction", "hType1", "hType0", "hTraitA", "hTraitR", "hTraitT1A",
                      "hTraitT1R", "hTraitT0A", "hTraitT0R"};
        Set headers = new HashSet<String>(Arrays.<String>asList(h));
        assertThat(mixed.getAction(ActionType.GET).getHeaders().keySet(), is(headers));
    }

    @Test
    public void override()
    {
        Resource override = raml.getResources().get("/override");
        assertThat(override.getActions().size(), is(1));
        Map<String,QueryParameter> queryParameters = override.getAction(ActionType.GET).getQueryParameters();
        assertThat(queryParameters.size(), is(9));
        assertThat(queryParameters.get("action").getName(), is("action"));
        assertThat(queryParameters.get("traitOverA").getName(), is("traitOverA"));
        assertThat(queryParameters.get("traitOverR").getName(), is("traitOverR"));
        assertThat(queryParameters.get("typeOverR").getName(), is("typeOverR"));
        assertThat(queryParameters.get("traitOverTypeA").getName(), is("traitOverTypeA"));
        assertThat(queryParameters.get("traitOverTypeR").getName(), is("traitOverTypeR"));
        assertThat(queryParameters.get("typeOverType").getName(), is("typeOverType"));
        assertThat(queryParameters.get("traitOverParentTypeA").getName(), is("traitOverParentTypeA"));
        assertThat(queryParameters.get("traitOverParentTypeR").getName(), is("traitOverParentTypeR"));
    }
}
