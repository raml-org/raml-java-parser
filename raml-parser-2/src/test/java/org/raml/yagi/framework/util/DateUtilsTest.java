package org.raml.yagi.framework.util;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * UT instad of full test, because this is becoming a problem.
 */
public class DateUtilsTest {

    @Test
    public void isValidDateDateOnly() {

        assertTrue(DateUtils.isValidDate("2002-01-01", DateType.date_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-1", DateType.date_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-1-01", DateType.date_only, "rfc3339"));
    }

    @Test
    public void isValidTimeOnly() {

        assertTrue(DateUtils.isValidDate("11:00:31", DateType.time_only, "rfc3339"));
        assertTrue(DateUtils.isValidDate("21:00:31", DateType.time_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("1:00:31", DateType.time_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("11:0:31", DateType.time_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("11:00:1", DateType.time_only, "rfc3339"));
    }

    @Test
    public void isValidDateTimeOnly() {

        assertTrue(DateUtils.isValidDate("2002-01-01T21:30:00", DateType.datetime_only, "rfc3339"));
        assertTrue(DateUtils.isValidDate("2002-01-01T21:30:00.001", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-01T21:30:00.", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-01T21:30:00.1234567890", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-0-01T21:30:00", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-0T21:30:00", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-01T2:30:00", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-01T21:3:00", DateType.datetime_only, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2002-01-01T21:30:0", DateType.datetime_only, "rfc3339"));
    }

    @Test
    public void isValidDateTimeRFC3339() {

        assertTrue(DateUtils.isValidDate("2019-09-15T12:38:34.107-04:00", DateType.datetime, "rfc3339"));
        assertTrue(DateUtils.isValidDate("2019-09-15T12:38:34.107Z", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-9-15T12:38:34.107-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-5T12:38:34.107-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-15T2:38:34.107-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-15T12:8:34.107-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-15T12:38:4.107-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-15T12:38:34.-04:00", DateType.datetime, "rfc3339"));
        assertFalse(DateUtils.isValidDate("2019-09-15T12:38:34.107-4:00", DateType.datetime, "rfc3339"));

        assertTrue(DateUtils.isValidDate("2019-09-15T12:38:34-04:00", DateType.datetime, "rfc3339"));
        assertTrue(DateUtils.isValidDate("2019-09-15T12:38:34Z", DateType.datetime, "rfc3339"));

    }

    @Test
    public void isValidDateTimeRFC2616() {

        assertTrue(DateUtils.isValidDate("Sun, 15 Sep 2019 13:03:23 GMT", DateType.datetime, "rfc2616"));
        assertFalse(DateUtils.isValidDate("Sun, 15 Sep 2019 3:03:23 GMT", DateType.datetime, "rfc2616"));

    }


}