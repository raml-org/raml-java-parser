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
package org.raml;

import java.io.FileNotFoundException;
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
            List<ValidationResult> results = RamlValidationService.createDefault().validate(loader.fetchResource(ramlResource), "");
            if (results.isEmpty())
            {
                System.out.println("\tOK.");
            }
            else
            {
                for (ValidationResult item : results)
                {
                    System.out.println(item.toDetailedString());
                }
            }
            System.out.println();
        }
    }

  

}