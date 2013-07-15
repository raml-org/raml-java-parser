package org.raml.parser.loader;

import java.io.InputStream;

public class DefaultResourceLoader implements ResourceLoader
{

    private ResourceLoader resourceLoader;

    public DefaultResourceLoader()
    {
        resourceLoader = new CompositeResourceLoader(
                new UrlResourceLoader(), new ClassPathResourceLoader());
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        return resourceLoader.fetchResource(resourceName);
    }
}
