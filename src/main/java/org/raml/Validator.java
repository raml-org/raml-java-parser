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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.raml.parser.rule.ValidationResult;
import org.raml.parser.visitor.RamlValidationService;

public class Validator
{

    public static void main(String[] args) throws FileNotFoundException
    {
        if (args.length == 0)
        {
            System.out.println("\n\tusage: java -jar raml-parser-{version}.jar raml-file ...\n");
            return;
        }
        new Validator().validate(args);
    }

    private void validate(String[] files) throws FileNotFoundException
    {
        for (String fileName : files)
        {
            File file = new File(fileName);
            InputStream stream = new FileInputStream(file);
            List<ValidationResult> results = RamlValidationService.createDefault().validate(stream, "");
            System.out.format("Validation Results for %s:\n", fileName);
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