package org.raml.parser.loader;

import java.io.InputStream;

public class ClassPathResourceLoader implements ResourceLoader
{

    @Override
    public InputStream fetchResource(String resourceName)
    {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null)
        {
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }
        return inputStream;
    }
}
