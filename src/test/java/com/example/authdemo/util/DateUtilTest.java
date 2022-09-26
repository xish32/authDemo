package com.example.authdemo.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class DateUtilTest {
    @Test
    public void testDateUtil() {
        Calendar curTime = Calendar.getInstance();
        curTime.set(2022, 8, 26, 21, 55, 00);
        curTime.getTime();

        Date newDate;
        newDate = DateUtil.getAddTime(curTime.getTime(), 2, Calendar.HOUR);
        System.out.println(newDate);

        checkDateHour(newDate, 26, 23, 55);

        newDate = DateUtil.getAddTime(newDate, 2, Calendar.HOUR);
        System.out.println(newDate);

        checkDateHour(newDate, 27, 1, 55);
    }

    private void checkDateHour(Date checkDate, int date, int hour, int minute) {
        assertNotEquals(null, checkDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(checkDate);

        assertEquals(date, cal.get(Calendar.DATE));
        assertEquals(hour, cal.get(Calendar.HOUR_OF_DAY));
        assertEquals(minute, cal.get(Calendar.MINUTE));
    }

}
