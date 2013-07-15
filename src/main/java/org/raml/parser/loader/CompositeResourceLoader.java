package org.raml.parser.loader;

import java.io.InputStream;

public class CompositeResourceLoader implements ResourceLoader
{

    private ResourceLoader[] resourceLoaders;

    public CompositeResourceLoader(ResourceLoader... resourceLoaders)
    {
        this.resourceLoaders = resourceLoaders;
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        InputStream inputStream = null;
        for (ResourceLoader loader : resourceLoaders)
        {
            inputStream = loader.fetchResource(resourceName);
            if (inputStream != null)
            {
                break;
            }
        }
        return inputStream;
    }
}
