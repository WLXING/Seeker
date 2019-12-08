package com.example.utils;

import java.util.Calendar;

/**
 * Created by ${WLX} on 2019/6/18.
 */

public class TimeUtil {
    public static long CountTime(int m_year,int m_month,int m_day,int m_hour,int m_minute) {
        Calendar currentCalendar=Calendar.getInstance();
        long nowTime=currentCalendar.getTimeInMillis();//当前时间
        Calendar myCalendar=Calendar.getInstance();
        myCalendar.set(m_year,m_month,m_day,m_hour,m_minute);
        long alarmTime=myCalendar.getTimeInMillis();//设定的时间
        return alarmTime-nowTime;
    }
}

