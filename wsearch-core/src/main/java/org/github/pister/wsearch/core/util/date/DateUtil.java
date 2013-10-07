package org.github.pister.wsearch.core.util.date;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午11:08
 */
public class DateUtil {

    public static Date parseDate(String input, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        try {
            return sdf.parse(input);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public static String formatDate(Date date, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    public static String formatDateYMDHMS(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date addSeconds(Date input, int seconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(input);
        cal.add(Calendar.SECOND, seconds);
        return cal.getTime();
    }
}
