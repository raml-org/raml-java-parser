package org.raml.parser.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileResourceLoader implements ResourceLoader
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private File parentPath;

    public FileResourceLoader(String path)
    {
        this(new File(path));
    }

    public FileResourceLoader(File path)
    {
        this.parentPath = path;
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        File includedFile = new File(parentPath, resourceName);
        FileInputStream inputStream = null;
        if (logger.isDebugEnabled())
        {
            logger.debug(String.format("Looking for resource: %s on directory: %s...", resourceName, parentPath));
        }
        try
        {
            return new FileInputStream(includedFile);
        }
        catch (FileNotFoundException e)
        {
            //ignore
        }
        return inputStream;
    }

}
