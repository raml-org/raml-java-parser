/*
 * Copyright (c) MuleSoft, Inc.
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
package org.raml.parser.rule;

import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.nodes.Node;

public class ValidationResult
{

    public enum Level
    {
        ERROR, WARN, INFO
    }

    private Level level;
    private String message;
    private Mark startMark;
    private Mark endMark;
    private String includeName;

    private ValidationResult(Level level, String message, Mark startMark, Mark endMark)
    {
        this.level = level;
        this.message = message;
        this.setStartMark(startMark);
        this.setEndMark(endMark);
    }

    public boolean isValid()
    {
        return level != Level.ERROR;
    }

    public String getMessage()
    {
        return message;
    }

    public static ValidationResult createErrorResult(String message, Mark startMark, Mark endMark)
    {
        return new ValidationResult(Level.ERROR, message, startMark, endMark);
    }

    public static ValidationResult createErrorResult(String message, Node node)
    {
        return createErrorResult(message, node.getStartMark(), node.getEndMark());
    }

    public static ValidationResult createErrorResult(String message)
    {
        return createErrorResult(message, null, null);
    }

    public static ValidationResult create(Level level, String message)
    {
        return new ValidationResult(level, message, null, null);
    }

    public Mark getStartMark()
    {
        return startMark;
    }

    public void setStartMark(Mark startMark)
    {
        this.startMark = startMark;
    }

    public Mark getEndMark()
    {
        return endMark;
    }

    public void setEndMark(Mark endMark)
    {
        this.endMark = endMark;
    }

    public String getIncludeName()
    {
        return includeName;
    }

    public void setIncludeName(String includeName)
    {
        this.includeName = includeName;
    }

    public static boolean areValid(List<ValidationResult> validationResults)
    {
        for (ValidationResult result : validationResults)
        {
            if (!result.isValid())
            {
                return false;
            }
        }
        return true;
    }

    public static List<ValidationResult> getLevel(Level level, List<ValidationResult> results)
    {
        List<ValidationResult> filtered = new ArrayList<ValidationResult>();
        for (ValidationResult result : results)
        {
            if (result.level == level)
            {
                filtered.add(result);
            }
        }
        return filtered;
    }

    @Override
    public String toString()
    {
        return " message='" + message + '\'' +
               "" + startMark +
               '}';
    }
}
