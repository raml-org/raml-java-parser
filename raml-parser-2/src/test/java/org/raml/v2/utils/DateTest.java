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
package org.raml.v2.utils;

import java.util.Locale;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;


public class DateTest
{
    @Test
    public void testSomething()
    {

        DateTimeFormatter hourFormatter = DateTimeFormat.forPattern("HH:mm:ss");
        DateTimeFormatter dateFormatter = DateTimeFormat.forPattern("YYYY-MM-DD");
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("YYYY-MM-DD'T'HH:mm:ss");
        DateTimeFormatter dateTimeSecondFormatter = DateTimeFormat.forPattern("YYYY-MM-DD'T'HH:mm:ss.SSS'Z'");
        DateTimeFormatter rfc = DateTimeFormat.forPattern("EEE, dd MMM yyyy HH:mm:ss zzz").withLocale(Locale.ENGLISH);

        Assert.assertNotNull(dateFormatter.parseLocalDate("2015-05-23"));
        Assert.assertNotNull(hourFormatter.parseLocalTime("12:30:00"));
        Assert.assertNotNull(dateTimeFormatter.parseLocalDateTime("2015-05-23T12:30:00"));
        Assert.assertNotNull(dateTimeSecondFormatter.parseLocalDateTime("2016-02-28T16:41:41.090Z"));
        Assert.assertNotNull(rfc.parseLocalDateTime("Sun, 28 Feb 2016 16:41:41 UTC"));

    }

}