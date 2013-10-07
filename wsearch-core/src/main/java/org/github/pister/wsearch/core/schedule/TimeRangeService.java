package org.github.pister.wsearch.core.schedule;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:57
 */
public interface TimeRangeService {

    TimeRange getTimeRange();

    void saveTimeRange(TimeRange timeRange);

    int getRangeStepSeconds();

    void setRangeStepSeconds(int rangeStepSeconds);
}
