package org.raml.includespreserving;

import org.raml.model.Raml2;
import org.raml.parser.builder.AbstractRamlTestCase;
import org.raml.parser.visitor.PreservingTemplatesBuilder;
import org.raml.parser.visitor.RamlDocumentBuilder;

public class IncludesPreservingRamlTestCase extends AbstractRamlTestCase{

	protected static Raml2 parseRaml(String resource)
    {
        return (Raml2) new PreservingTemplatesBuilder().build(getInputStream(resource));
    }

    protected static Raml2 parseRaml(String resource, RamlDocumentBuilder builder)
    {
        return (Raml2) builder.build(getInputStream(resource));
    }
}
