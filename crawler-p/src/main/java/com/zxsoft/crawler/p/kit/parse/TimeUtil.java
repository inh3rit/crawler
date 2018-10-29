package com.zxsoft.crawler.p.kit.parse;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cox on 2015/10/22.
 */
public class TimeUtil {


    public static Calendar strToCalendar(String time, String format) {
        SimpleDateFormat dateFormat=new SimpleDateFormat(format);
        try {
            Date date=dateFormat.parse(time);
            Calendar calendar=Calendar.getInstance();
            calendar.setTime(date);
            // System.out.println(calendar.get(Calendar.YEAR));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


}
