package org.github.pister.wsearch.core.schedule;

import java.util.Calendar;
import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午11:51
 */
public class RamTimeRangeService implements TimeRangeService {

    private TimeRange timeRange;
    private int rangeStepSeconds;

    public RamTimeRangeService(int rangeStepSeconds) {
        timeRange = new TimeRange();
        timeRange.setStart(new Date(0));
        timeRange.setEnd(new Date());
        this.rangeStepSeconds = rangeStepSeconds;
    }

    static Date getTimeAfter(Date date, int rangeStepSeconds) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, rangeStepSeconds);
        return cal.getTime();
    }

    @Override
    public TimeRange getTimeRange() {
        TimeRange ret = new TimeRange();
        ret.setStart(new Date(timeRange.getStart().getTime()));
        ret.setEnd(new Date(timeRange.getEnd().getTime()));
        return ret;
    }

    @Override
    public int getRangeStepSeconds() {
        return rangeStepSeconds;
    }

    @Override
    public void nextTimeRange() {
        TimeRange newTimeRange = new TimeRange();

        Date newStartDate = new Date(timeRange.getEnd().getTime());
        newTimeRange.setStart(newStartDate);
        newTimeRange.setEnd(getTimeAfter(newStartDate, rangeStepSeconds));

        this.timeRange = newTimeRange;
    }
}
