package com.zxsoft.crawler.p.test.time;

import org.junit.Test;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by cox on 2015/10/22.
 */
public class TimeUtil {


    public static Calendar strToCalendar(String time, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            Date date = dateFormat.parse(time);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // System.out.println(calendar.get(Calendar.YEAR));
            return calendar;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Test
    public void isToday() {
        LocalDate localDate = LocalDate.now();
        String url_reg = MessageFormat.format("/{0}/{1}/{2}/", Integer.valueOf(localDate.getYear()).toString().substring(2), localDate.getMonthValue(), localDate.getDayOfMonth());
        System.out.println(url_reg);
//        Pattern p = Pattern.compile(url_reg);
//        Matcher m = p.matcher(url);
//        return m.find();
    }
}
