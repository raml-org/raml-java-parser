/*
 * Copyright 2013 (c) MuleSoft, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 */
package org.raml.v2.internal.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.raml.v2.internal.impl.emitter.tck.TckEmitter;
import org.raml.v2.internal.utils.RamlNodeUtils;
import org.raml.yagi.framework.nodes.ErrorNode;
import org.raml.yagi.framework.nodes.Node;
import org.raml.yagi.framework.nodes.Position;

public class RamlValidator
{

    public static final String USAGE = "Arguments: [-dump] file|url|dir";

    private boolean dump;
    private String ramlLocation;
    private int ramlCount;
    private int validRamlCount;

    public RamlValidator(String[] args)
    {
        parseArguments(args);
    }

    private void validate()
    {
        validate(new File(ramlLocation));
        if (ramlCount > 1)
        {
            System.out.println(StringUtils.repeat("=", 50));
            System.out.format("Parsed %d raml files. %d OK, %d with Errors.\n", ramlCount, validRamlCount, ramlCount - validRamlCount);
            System.out.println(StringUtils.repeat("=", 50));
        }
    }

    private void validate(File location)
    {
        if (isRamlFile(location))
        {
            validateRaml(location);
        }

        File[] files = new File[] {};
        if (location.isDirectory())
        {
            files = location.listFiles(new FileFilter()
            {
                @Override
                public boolean accept(File pathname)
                {
                    return pathname.isDirectory() || isRamlFile(pathname);
                }
            });
        }
        for (File file : files)
        {
            validate(file);
        }
    }

    private void validateRaml(File ramlFile)
    {
        System.out.println(StringUtils.repeat("=", 120));
        System.out.println(ramlFile);
        System.out.println(StringUtils.repeat("=", 120));

        ramlCount++;
        final Node raml = new RamlBuilder().build(ramlFile);
        List<ErrorNode> errors = RamlNodeUtils.getErrors(raml);
        if (!errors.isEmpty())
        {
            logErrors(errors);
            return;
        }

        validRamlCount++;
        if (dump)
        {
            String json = new TckEmitter().dump(raml);
            System.out.println(StringUtils.repeat("=", 120));
            System.out.println(json);
            System.out.println(StringUtils.repeat("=", 120));
        }
        else
        {
            System.out.println("No errors found.");
        }

    }

    private boolean isRamlFile(File pathname)
    {
        return pathname.isFile() && pathname.getName().endsWith(".raml");
    }

    public static void main(String[] args) throws IOException
    {
        new RamlValidator(args).validate();
    }

    private void logErrors(List<ErrorNode> errors)
    {
        String label = errors.size() > 1 ? "errors" : "error";
        System.out.format("%d %s found:\n\n", errors.size(), label);
        for (ErrorNode error : errors)
        {
            String message = error.getErrorMessage();
            int idx = message.indexOf(". Options are");
            if (idx != -1)
            {
                message = message.substring(0, idx);
            }
            Position position = error.getSource() != null ? error.getSource().getStartPosition() : error.getStartPosition();
            System.out.format("\t- %s %s\n\n", message, position);
        }
    }

    private void parseArguments(String[] args)
    {
        if (args.length < 1 || args.length > 2)
        {
            throw new IllegalArgumentException(USAGE);
        }
        if (args.length == 2)
        {
            if (!"-dump".equals(args[0]))
            {
                throw new IllegalArgumentException(USAGE);
            }
            this.dump = true;
            this.ramlLocation = args[1];
        }
        else
        {
            this.ramlLocation = args[0];
        }
    }

}
