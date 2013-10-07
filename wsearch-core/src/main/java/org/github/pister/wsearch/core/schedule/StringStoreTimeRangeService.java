package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.util.date.DateUtil;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午2:23
 */
public abstract class StringStoreTimeRangeService extends AbstractTimeRangeService {

    private static final String DATE_FMT = "yyyy-MM-dd HH:mm:ss";

    protected TimeRange decode(String data) {
        String[] parts = data.split("/");
        if (parts.length < 2) {
            throw new RuntimeException("error format string data:" + data + ", expect: " + DATE_FMT + "/" + DATE_FMT);
        }
        Date startDate = DateUtil.parseDate(parts[0], DATE_FMT);
        Date endDate = DateUtil.parseDate(parts[1], DATE_FMT);
        return new TimeRange(startDate, endDate);
    }

    protected String encode(TimeRange timeRange) {
        Date start = timeRange.getStart();
        Date end = timeRange.getEnd();
        String s1 = DateUtil.formatDate(start, DATE_FMT);
        String s2 = DateUtil.formatDate(end, DATE_FMT);
        return s1 + "/" + s2;
    }

    protected abstract void store(String content);

    protected abstract String load();

    @Override
    protected final void storeTimeRange(TimeRange timeRange) {
        store(encode(timeRange));
    }

    @Override
    protected final TimeRange loadTimeRange() {
        String data = load();
        if (data == null) {
            TimeRange defaultValue = this.getDefaultTimeRange();
            storeTimeRange(defaultValue);
            return defaultValue;
        }
        return decode(data);
    }


}
