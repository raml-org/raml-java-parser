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
package org.raml.v2.internal.utils;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

import com.google.common.base.CaseFormat;

public class Inflector
{

    public static String singularize(String word)
    {
        return InflectorBase.singularize(word);
    }

    public static String pluralize(String word)
    {
        return InflectorBase.pluralize(word);
    }

    public static String uppercase(String word)
    {
        return word.toUpperCase();
    }

    public static String lowercase(String word)
    {
        return word.toLowerCase();
    }

    public static String lowercamelcase(String word)
    {
        return convert(word, LOWER_CAMEL);
    }

    public static String uppercamelcase(String word)
    {
        return convert(word, UPPER_CAMEL);
    }

    public static String lowerunderscorecase(String word)
    {
        return convert(word, LOWER_UNDERSCORE);
    }

    public static String upperunderscorecase(String word)
    {
        return convert(word, UPPER_UNDERSCORE);
    }

    public static String lowerhyphencase(String word)
    {
        return convert(word, LOWER_HYPHEN);
    }

    public static String upperhyphencase(String word)
    {
        String convert = convert(word, UPPER_UNDERSCORE);
        return convert.replace("_", "-");
    }

    private static String convert(String word, CaseFormat format)
    {
        word = normalize(word);
        return detectFormat(word).to(format, word);
    }

    private static String normalize(String word)
    {
        return word.replace("-", "_");
    }

    private static CaseFormat detectFormat(String word)
    {
        boolean allUpper = word.toUpperCase().equals(word);
        boolean allLower = word.toLowerCase().equals(word);
        boolean mixedCase = !allUpper && !allLower;
        boolean firstCapital = word.substring(0, 1).toUpperCase().equals(word.substring(0, 1));
        if (mixedCase)
        {
            if (firstCapital)
            {
                return CaseFormat.UPPER_CAMEL;
            }
            return LOWER_CAMEL;
        }
        if (allUpper)
        {
            return CaseFormat.UPPER_UNDERSCORE;
        }
        return CaseFormat.LOWER_UNDERSCORE;
    }

}
