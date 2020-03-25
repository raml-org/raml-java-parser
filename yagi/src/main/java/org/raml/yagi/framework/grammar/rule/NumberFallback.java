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
package org.raml.yagi.framework.grammar.rule;

/**
 * Created. There, you have it.
 */
public class NumberFallback
{
    private static final String CAST_STRINGS_AS_NUMBERS_PROP = "org.raml.cast_strings_as_number";
    public static boolean CAST_STRINGS_AS_NUMBERS = Boolean.parseBoolean(System.getProperty(CAST_STRINGS_AS_NUMBERS_PROP, "false"));
}
