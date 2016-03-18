/*
 * Copyright 2016 (c) MuleSoft, Inc.
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
package org.raml;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.raml.parser.loader.DefaultResourceLoader;
import org.raml.parser.loader.ResourceLoader;
import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class Validator
{

    public static void main(String[] args) throws FileNotFoundException
    {
        if (args.length == 0)
        {
            System.out.println("\n\tusage: java -jar raml-parser-{version}.jar raml-resource ...\n");
            return;
        }
        new Validator().validate(args);
    }

    private void validate(String[] resources) throws FileNotFoundException
    {
        ResourceLoader loader = new DefaultResourceLoader();
        for (String ramlResource : resources)
        {
            System.out.format("Validation Results for %s:\n", ramlResource);
            String ramlResourceToFetch = ramlResource;
            if (ramlResource.startsWith("/"))
            {
                ramlResourceToFetch = "file://" + ramlResource;
            }
            List<ValidationResult> results = new ArrayList<ValidationResult>();
            InputStream content = loader.fetchResource(ramlResourceToFetch);
            if (content != null)
            {
                results = RamlValidationService.createDefault().validate(content, ramlResourceToFetch);
            }
            else
            {
                results.add(ValidationResult.createErrorResult("Raml resource not found: " + ramlResource));
            }
            if (results.isEmpty())
            {
                System.out.println("\tOK.");
            }
            else
            {
                for (ValidationResult item : results)
                {
                    printResult(item);
                }
            }
            System.out.println();
        }
    }

    private void printResult(ValidationResult item)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\t");
        stringBuilder.append(item.getLevel());
        stringBuilder.append(" ");
        stringBuilder.append(item.getMessage());
        if (item.getLine() != -1)
        {
            stringBuilder.append(" (line ");
            stringBuilder.append(item.getLine());
            if (item.getStartColumn() != -1)
            {
                stringBuilder.append(", col ");
                stringBuilder.append(item.getStartColumn());
                if (item.getEndColumn() != item.getStartColumn())
                {
                    stringBuilder.append(" to ");
                    stringBuilder.append(item.getEndColumn());
                }
            }
            stringBuilder.append(")");
        }
        System.out.println(stringBuilder.toString());
    }

}