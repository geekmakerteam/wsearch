package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.util.date.DateUtil;

import java.util.Date;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午2:01
 */
public abstract class AbstractTimeRangeService implements TimeRangeService {

    protected abstract void storeLastTime(Date timeRange);

    protected abstract Date loadLastTime();

    private int rangeStepSeconds = 60;

    @Override
    public TimeRange getTimeRange() {
        Date date = loadLastTime();
        return new TimeRange(date, new Date());
    }

    @Override
    public void saveTimeRange(TimeRange timeRange) {
        Date oldTime = timeRange.getEnd();
        storeLastTime(oldTime);
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
