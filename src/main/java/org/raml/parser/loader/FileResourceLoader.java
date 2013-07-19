package org.raml.parser.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FileResourceLoader implements ResourceLoader
{

    private File currentFolder;

    public FileResourceLoader(File currentFolder)
    {
        this.currentFolder = currentFolder;
        
    }

    @Override
    public InputStream fetchResource(String resourceName)
    {
        File includedFile = new File(currentFolder, resourceName);
        FileInputStream inputStream = null;
        try
        {
            return new FileInputStream(includedFile);
        }
        catch (FileNotFoundException e)
        {
            
        }
        return inputStream;
    }

}
