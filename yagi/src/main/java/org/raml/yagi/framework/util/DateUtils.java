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
    private static final String STRICT_DATES = "org.raml.strict_dates";

    public static boolean FOUR_YEARS_VALIDATION = Boolean.parseBoolean(System.getProperty(DATE_ONLY_FOUR_DIGITS_YEAR_LENGTH_VALIDATION, "true"));
    public static boolean STRICT_DATES_VALIDATION = Boolean.parseBoolean(System.getProperty(STRICT_DATES, "true"));

    private DateUtils(boolean strictYear, boolean strictDates)
    {
        setFormatters(strictYear, strictDates);
    }

    public static DateUtils createStrictDateUtils()
    {
        return new DateUtils(true, true);
    }

    public static DateUtils createNonStrictDateUtils()
    {
        return new DateUtils(false, false);
    }

    public static DateUtils createFromProperties()
    {
        return new DateUtils(FOUR_YEARS_VALIDATION, STRICT_DATES_VALIDATION);
    }

    public void setFormatters(boolean strictYear, boolean strictDates)
    {

        dateOnlyFormatter = yearMonthDayFormat(strictYear, strictDates).toFormatter();
        timeOnlyFormatter = timeOnlyFormatter(strictDates).toFormatter();
        dateTimeOnlyFormatterNoMillis = dateTimeFormat(strictYear, strictDates).toFormatter();
        dateTimeOnlyFormatterMillis = dateTimeFormat(strictYear, strictDates)
                                                                             .appendLiteral(".")
                                                                             .appendFractionOfSecond(1, 9)
                                                                             .toFormatter();

        rfc2616Formatter = new DateTimeFormatterBuilder()
                                                         .append(DateTimeFormat.forPattern("EEE, dd MMM "))
                                                         .append(yearFormat(strictYear, strictDates).toFormatter())
                                                         .appendLiteral(' ')
                                                         .append(timeOnlyFormatter(strictDates).toFormatter())
                                                         .append(DateTimeFormat.forPattern(" zzz")).toFormatter();

        rfc3339FormatterMillis = dateTimeFormat(strictYear, strictDates)
                                                                        .appendLiteral(".")
                                                                        .appendFractionOfSecond(1, 9)
                                                                        .appendTimeZoneOffset("Z", true, 2, 4)
                                                                        .toFormatter();
        rfc3339FormatterNoMillis = dateTimeFormat(strictYear, strictDates)
                                                                          .appendTimeZoneOffset("Z", true, 2, 4)
                                                                          .toFormatter();
    }

    private DateTimeFormatterBuilder yearMonthDayFormat(boolean strictYear, boolean strictDates)
    {
        if (strictDates)
        {
            return yearFormat(strictYear, strictDates).appendLiteral('-')
                                                      .appendFixedDecimal(DateTimeFieldType.monthOfYear(), 2)
                                                      .appendLiteral('-')
                                                      .appendFixedDecimal(DateTimeFieldType.dayOfMonth(), 2);
        }
        else
        {
            return yearFormat(strictYear, strictDates).appendLiteral('-')
                                                      .appendDecimal(DateTimeFieldType.monthOfYear(), 1, 2)
                                                      .appendLiteral('-')
                                                      .appendDecimal(DateTimeFieldType.dayOfMonth(), 1, 2);

        }
    }

    private DateTimeFormatterBuilder dateTimeFormat(boolean strictYear, boolean strictDates)
    {
        return yearMonthDayFormat(strictYear, strictDates).appendLiteral('T').append(timeOnlyFormatter(strictDates).toFormatter());
    }

    private DateTimeFormatterBuilder timeOnlyFormatter(boolean strictDates)
    {
        if (strictDates)
        {
            return new DateTimeFormatterBuilder()
                                                 .appendFixedDecimal(DateTimeFieldType.hourOfDay(), 2).appendLiteral(':')
                                                 .appendFixedDecimal(DateTimeFieldType.minuteOfHour(), 2).appendLiteral(':')
                                                 .appendFixedDecimal(DateTimeFieldType.secondOfMinute(), 2);
        }
        else
        {

            return new DateTimeFormatterBuilder()
                                                 .appendDecimal(DateTimeFieldType.hourOfDay(), 1, 2).appendLiteral(':')
                                                 .appendDecimal(DateTimeFieldType.minuteOfHour(), 1, 2).appendLiteral(':')
                                                 .appendDecimal(DateTimeFieldType.secondOfMinute(), 1, 2);
        }
    }

    private DateTimeFormatter dateOnlyFormatter;
    private DateTimeFormatter timeOnlyFormatter;

    private DateTimeFormatter dateTimeOnlyFormatterNoMillis;
    private DateTimeFormatter dateTimeOnlyFormatterMillis;

    private DateTimeFormatter rfc3339FormatterMillis;
    private DateTimeFormatter rfc3339FormatterNoMillis;

    private DateTimeFormatter rfc2616Formatter;


    public boolean isValidDate(String date, DateType format, String rfc)
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

    private DateTimeFormatterBuilder yearFormat(boolean strictYear, boolean strictDates)
    {

        if (strictYear || strictDates)
        {

            return new DateTimeFormatterBuilder().appendFixedSignedDecimal(DateTimeFieldType.year(), 4);
        }
        else
        {

            return new DateTimeFormatterBuilder().append(DateTimeFormat.forPattern("YYYY"));
        }
    }
}