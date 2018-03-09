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
package org.raml.yagi.framework.util;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class DateUtils
{
    private static DateTimeFormatter dateOnlyFormatter = DateTimeFormat.forPattern("YYYY-MM-dd");
    private static DateTimeFormatter timeOnlyFormatter = DateTimeFormat.forPattern("HH:mm:ss");
    private static DateTimeFormatter dateTimeOnlyFormatter = DateTimeFormat.forPattern("YYYY-MM-DD'T'HH:mm:ss");
    private static DateTimeFormatter rfc2616Formatter = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz");
    private static DateTimeFormatter rfc3339FormatterMillis = ISODateTimeFormat.dateTime();
    private static DateTimeFormatter rfc3339FormatterNoMillis = ISODateTimeFormat.dateTimeNoMillis();

    public static final String DATE_ONLY_FOUR_DIGITS_YEAR_LENGTH_VALIDATION = "yagi.date_only_four_digits_year_length_validation";
    public static boolean FOUR_YEARS_VALIDATION = Boolean.valueOf(System.getProperty(DATE_ONLY_FOUR_DIGITS_YEAR_LENGTH_VALIDATION, "true"));

    public static boolean isValidDate(String date, DateType format, String rfc)
    {
        try
        {
            switch (format)
            {
            case date_only:
                if (!hasFourDigitsYear(date.split("-")) && FOUR_YEARS_VALIDATION)
                    throw new IllegalArgumentException();
                dateOnlyFormatter.parseLocalDate(date);
                break;
            case time_only:
                timeOnlyFormatter.parseLocalTime(date);
                break;
            case datetime_only:
                dateTimeOnlyFormatter.parseLocalDateTime(date);
                break;
            case datetime:
                if (rfc != null && "rfc2616".equals(rfc))
                {
                    rfc2616Formatter.parseLocalDateTime(date);
                    break;
                }
                else
                {
                    try
                    {
                        rfc3339FormatterMillis.parseLocalDateTime(date);
                    }
                    catch (Exception e)
                    {
                        rfc3339FormatterNoMillis.parseLocalDateTime(date);
                    }
                    break;
                }
            default:
                return false;
            }
            return true;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private static boolean hasFourDigitsYear(String[] splitted)
    {
        return splitted.length > 0 && splitted[0].length() == 4;
    }
}