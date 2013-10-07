package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.util.date.DateUtil;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午2:01
 */
public abstract class AbstractTimeRangeService implements TimeRangeService {

    protected abstract void storeTimeRange(TimeRange timeRange);

    protected abstract TimeRange loadTimeRange();

    private int rangeStepSeconds = 60;

    @Override
    public TimeRange getTimeRange() {
        return loadTimeRange();
    }

    @Override
    public void nextTimeRange() {
        TimeRange oldTimeRange = getTimeRange();
        TimeRange newTimeRange = new TimeRange();

        Date newStartDate = new Date(oldTimeRange.getEnd().getTime());
        newTimeRange.setStart(newStartDate);
        newTimeRange.setEnd(DateUtil.addSeconds(newStartDate, rangeStepSeconds));

        storeTimeRange(newTimeRange);
    }

    @Override
    public int getRangeStepSeconds() {
        return rangeStepSeconds;
    }

    @Override
    public void setRangeStepSeconds(int rangeStepSeconds) {
        this.rangeStepSeconds = rangeStepSeconds;
    }

    protected TimeRange getDefaultTimeRange() {
        return new TimeRange(new Date(0), new Date());
    }
}
