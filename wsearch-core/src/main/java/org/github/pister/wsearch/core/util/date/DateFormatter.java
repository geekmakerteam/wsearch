package org.github.pister.wsearch.core.util.date;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: longyi
 * Date: 13-9-30
 * Time: 上午11:08
 */
public class DateFormatter {

    public static String formatDate(Date date, String fmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(fmt);
        return sdf.format(date);
    }

    public static String formatDateYMDHMS(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }
}
