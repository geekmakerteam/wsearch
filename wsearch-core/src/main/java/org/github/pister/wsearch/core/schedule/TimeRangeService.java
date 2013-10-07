package org.github.pister.wsearch.core.schedule;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:57
 */
public interface TimeRangeService {

    TimeRange nextTimeRange();

    void saveTimeRange(TimeRange timeRange);

}
