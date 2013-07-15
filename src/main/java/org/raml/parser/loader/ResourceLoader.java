package org.raml.parser.loader;

import java.io.InputStream;

public interface ResourceLoader
{

    InputStream fetchResource(String resourceName);

}
