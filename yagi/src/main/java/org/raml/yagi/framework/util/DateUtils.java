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

import org.joda.time.DateTimeFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.ISODateTimeFormat;

import static org.joda.time.format.ISODateTimeFormat.*;

public class DateUtils
{
    private static final String DATE_ONLY_FOUR_DIGITS_YEAR_LENGTH_VALIDATION = "yagi.date_only_four_digits_year_length_validation";
    public static boolean FOUR_YEARS_VALIDATION = Boolean.parseBoolean(System.getProperty(DATE_ONLY_FOUR_DIGITS_YEAR_LENGTH_VALIDATION, "true"));

    static
    {
        setFormatters();
    }

    public static void setFormatters()
    {

        dateOnlyFormatter = yearMonthDayFormat().toFormatter();
        timeOnlyFormatter = timeOnlyFormatter().toFormatter();
        dateTimeOnlyFormatterNoMillis = dateTimeFormat().toFormatter();
        dateTimeOnlyFormatterMillis = dateTimeFormat()
                                                      .appendLiteral(".")
                                                      .appendFractionOfSecond(1, 9)
                                                      .toFormatter();

        rfc2616Formatter = new DateTimeFormatterBuilder()
                .append(DateTimeFormat.forPattern("EEE, dd MMM "))
                .appendDecimal(DateTimeFieldType.year(), 2, 4)
                .appendLiteral(' ')
                .append(timeOnlyFormatter().toFormatter())
                .append(DateTimeFormat.forPattern(" zzz")).toFormatter();
        rfc3339FormatterMillis = dateTimeFormat()
                                                 .appendLiteral(".")
                                                 .appendFractionOfSecond(1, 9)
                                                 .appendTimeZoneOffset("Z", true, 2, 4)
                                                 .toFormatter();
        rfc3339FormatterNoMillis = dateTimeFormat()
                                                   .appendTimeZoneOffset("Z", true, 2, 4)
                                                   .toFormatter();
    }

    private static DateTimeFormatterBuilder yearMonthDayFormat()
    {
        return yearFormat().appendLiteral('-')
                           .appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2)
                           .appendLiteral('-')
                           .appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2);
    }

    private static DateTimeFormatterBuilder dateTimeFormat()
    {
        return yearMonthDayFormat().appendLiteral('T').append(timeOnlyFormatter().toFormatter());
    }

    private static DateTimeFormatterBuilder timeOnlyFormatter()
    {
        return new DateTimeFormatterBuilder()
                                             .appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendLiteral(':')
                                             .appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendLiteral(':')
                                             .appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2);

    }

    private static DateTimeFormatter dateOnlyFormatter;
    private static DateTimeFormatter timeOnlyFormatter;

    private static DateTimeFormatter dateTimeOnlyFormatterNoMillis;
    private static DateTimeFormatter dateTimeOnlyFormatterMillis;

    private static DateTimeFormatter rfc3339FormatterMillis;
    private static DateTimeFormatter rfc3339FormatterNoMillis;

    private static DateTimeFormatter rfc2616Formatter;


    public static boolean isValidDate(String date, DateType format, String rfc)
    {
        try
        {
            switch (format)
            {
            case date_only:
                dateOnlyFormatter.parseLocalDate(date);
                break;
            case time_only:
                timeOnlyFormatter.parseLocalTime(date);
                break;
            case datetime_only:
                try
                {
                    dateTimeOnlyFormatterNoMillis.parseLocalDateTime(date);
                }
                catch (Exception e)
                {
                    dateTimeOnlyFormatterMillis.parseLocalDateTime(date);
                }
                break;
            case datetime:
                if ("rfc2616".equals(rfc))
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

    private static DateTimeFormatterBuilder yearFormat()
    {

        if (FOUR_YEARS_VALIDATION)
        {

            return new DateTimeFormatterBuilder().appendFixedSignedDecimal(DateTimeFieldType.year(), 4);
        }
        else
        {

            return new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern("YYYY"));
        }
    }
}