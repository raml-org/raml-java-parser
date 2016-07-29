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

import java.util.Stack;

public class UriTemplateValidation
{
    private static final char OPEN_BRACE = '{';
    private static final char CLOSE_BRACE = '}';

    public static boolean isBalanced(String value)
    {
        Stack<Character> characterStack = new Stack<Character>();

        for (int i = 0; i < value.length(); i++)
        {
            if (value.charAt(i) == OPEN_BRACE)
            {
                if (characterStack.isEmpty())
                {
                    characterStack.push(OPEN_BRACE);
                }
                else
                {
                    // We have found a nested brace
                    return false;
                }

            }
            else if (value.charAt(i) == CLOSE_BRACE)
            {
                if (characterStack.isEmpty())
                    return false;
                if (characterStack.pop() != OPEN_BRACE)
                    return false;
            }
        }

        // If the stack is empty is because all open braces has its corresponding close brace and the string is balanced
        return characterStack.isEmpty();
    }
}