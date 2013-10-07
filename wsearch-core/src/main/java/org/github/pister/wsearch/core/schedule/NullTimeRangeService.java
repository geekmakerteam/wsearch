package org.github.pister.wsearch.core.schedule;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午11:51
 */
public class NullTimeRangeService implements TimeRangeService {
    @Override
    public TimeRange nextTimeRange() {
        return null;
    }

    @Override
    public void saveTimeRange(TimeRange timeRange) {

    }
}
