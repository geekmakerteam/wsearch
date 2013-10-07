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

    protected Date decode(String data) {
        return DateUtil.parseDate(data, DATE_FMT);
    }

    protected String encode(Date date) {
        return DateUtil.formatDate(date, DATE_FMT);
    }

    protected abstract void store(String content);

    protected abstract String load();

    @Override
    protected final void storeLastTime(Date timeRange) {
        store(encode(timeRange));
    }

    @Override
    protected final Date loadLastTime() {
        String data = load();
        if (data == null) {
            TimeRange defaultValue = this.getDefaultTimeRange();
            return defaultValue.getStart();
        }
        return decode(data);
    }


}
