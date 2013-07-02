package org.raml.parser.builder;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.raml.model.Raml;

public class IncludeTestCase extends AbstractBuilderTestCase
{

    @Test
    public void testNotFound()
    {
        try
        {
            parseRaml("org/raml/include/include-not-found.yaml");
            fail();
        }
        catch (Exception e)
        {
            assertThat(e.getMessage(), startsWith("resource not found"));
        }
    }

    @Test
    public void testSingleLineString()
    {
        Raml raml = parseRaml("org/raml/include/include-non-yaml-single-line.yaml");
        assertThat(raml.getTitle(), is("included title"));
    }


    @Test
    public void testMultiLineString() throws Exception
    {
        Raml raml = parseRaml("org/raml/include/include-non-yaml-multi-line.yaml");
        String multiLine = IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream("org/raml/include/include-non-yaml-multi-line.txt"));
        assertThat(raml.getTitle(), is(multiLine));
    }

    @Test
    public void testYaml()
    {
        Raml raml = parseRaml("org/raml/include/include-yaml.yaml");
        assertThat(raml.getDocumentation().size(), is(2));
        assertThat(raml.getDocumentation().get(0).getTitle(), is("Home"));
        assertThat(raml.getDocumentation().get(0).getContent(), startsWith("Lorem ipsum"));
        assertThat(raml.getDocumentation().get(1).getTitle(), is("Section"));
        assertThat(raml.getDocumentation().get(1).getContent(), is("section content"));
    }

    @Test
    @Ignore //use local http server
    public void testHttpScalarResource()
    {
        Raml raml = parseRaml("org/raml/include/include-http-non-yaml.yaml");
        assertThat(raml.getDocumentation().size(), is(1));
        assertThat(raml.getDocumentation().get(0).getTitle(), is("Home"));
        assertThat(raml.getDocumentation().get(0).getContent(), startsWith("Stop the point-to-point madness"));
    }

}
