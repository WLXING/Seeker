package com.example.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ${WLX} on 2019/6/14.
 */

public class DateUtil {
    public static String Date2String(Date date) {
        String dateString = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        dateString = sdf.format(date);
        return dateString;
    }

}
