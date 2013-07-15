package org.raml.parser.loader;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class UrlResourceLoader implements ResourceLoader
{

    @Override
    public InputStream fetchResource(String resourceName)
    {
        InputStream inputStream = null;
        try
        {
            URL url = new URL(resourceName);
            inputStream = new BufferedInputStream(url.openStream());
        }
        catch (IOException e)
        {
            //ignore on resource not found
        }
        return inputStream;

    }
}
