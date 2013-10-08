package org.github.pister.wsearch.core.schedule.timerange;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:57
 */
public interface TimeRangeService {

    TimeRange getTimeRange();

    void saveTimeRange(TimeRange timeRange);

}
