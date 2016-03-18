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
package org.raml.model;

import java.math.BigDecimal;

import org.raml.model.parameter.AbstractParam;

public enum ParamType
{
    STRING
            {
                @Override
                public String message(AbstractParam param, String value)
                {
                    if (param.getPattern() != null && !value.matches(param.getPattern()))
                    {
                        return "Value does not match pattern " + param.getPattern();
                    }
                    if (param.getMinLength() != null && value.length() < param.getMinLength())
                    {
                        return "Value length is shorter than " + param.getMinLength();
                    }
                    if (param.getMaxLength() != null && value.length() > param.getMaxLength())
                    {
                        return "Value length is longer than " + param.getMaxLength();
                    }
                    if (param.getEnumeration() != null && !param.getEnumeration().contains(value))
                    {
                        return "Value must be one of " + param.getEnumeration();
                    }
                    return OK;
                }
            },
    NUMBER
            {
                @Override
                public String message(AbstractParam param, String value)
                {
                    BigDecimal number;
                    try
                    {
                        number = new BigDecimal(value);
                    }
                    catch (NumberFormatException nfe)
                    {
                        return "Number required";
                    }
                    if (param.getMinimum() != null && number.compareTo(param.getMinimum()) < 0)
                    {
                        return "Value is below the minimum " + param.getMinimum();
                    }
                    if (param.getMaximum() != null && number.compareTo(param.getMaximum()) > 0)
                    {
                        return "Value is above the maximum " + param.getMaximum();
                    }
                    return OK;
                }
            },
    INTEGER
            {
                @Override
                public String message(AbstractParam param, String value)
                {
                    Integer number;
                    try
                    {
                        number = Integer.parseInt(value);
                    }
                    catch (NumberFormatException nfe)
                    {
                        return "Integer required";
                    }
                    if (param.getMinimum() != null && BigDecimal.valueOf(number).compareTo(param.getMinimum()) < 0)
                    {
                        return "Value is below the minimum " + param.getMinimum();
                    }
                    if (param.getMaximum() != null && BigDecimal.valueOf(number).compareTo(param.getMaximum()) > 0)
                    {
                        return "Value is above the maximum " + param.getMaximum();
                    }
                    return OK;
                }
            },
    DATE, //TODO add date validation
    FILE,
    BOOLEAN
            {
                @Override
                public String message(AbstractParam param, String value)
                {
                    if ("true".equals(value) || "false".equals(value))
                    {
                        return OK;
                    }
                    return "Value must be one of [true, false]";
                }
            };

    public boolean validate(AbstractParam param, String value)
    {
        return OK.equals(message(param, value));
    }

    public String message(AbstractParam param, String value)
    {
        return OK;
    }

    public static final String OK = "OK";

}
