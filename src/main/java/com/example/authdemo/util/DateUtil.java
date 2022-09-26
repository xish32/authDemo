package com.example.authdemo.util;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateUtil {
    public static Date getAddTime(Date curDate, int count, int timeUnit) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(curDate);

        cal.add(timeUnit, count);
        return cal.getTime();
    }
}
