package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.schedule.timerange.TimeRange;
import org.github.pister.wsearch.core.schedule.timerange.TimeRangeAwire;
import org.github.pister.wsearch.core.schedule.timerange.TimeRangeService;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 下午12:52
 */
public abstract class IncrIndexDumpScheduler extends AbstractIndexDumpScheduler {

    private static final String TIME_RANGE_NAME = "time_range_name";
    private int delayInSeconds;
    private TimeRangeService timeRangeService;

    protected IncrIndexDumpScheduler(int delayInSeconds, TimeRangeService timeRangeService) {
        this.delayInSeconds = delayInSeconds;
        this.timeRangeService = timeRangeService;
        this.timeRangeService.setRangeStepSeconds(delayInSeconds);
    }

    @Override
    protected void onBeforeDump(DumpContext dumpContext) {
        super.onBeforeDump(dumpContext);
        DataProvider dataProvider = this.dataProvider;
        TimeRange timeRange = getTimeRangeService().getTimeRange();
        if (dataProvider instanceof TimeRangeAwire) {
            ((TimeRangeAwire) dataProvider).setTimeRange(timeRange);
            dumpContext.setAttribute(TIME_RANGE_NAME, timeRange);
        }
    }

    @Override
    protected void onAfterDump(DumpContext dumpContext) {
        super.onAfterDump(dumpContext);
        getTimeRangeService().saveTimeRange((TimeRange) dumpContext.getAttribute(TIME_RANGE_NAME));
    }

    protected TimeRangeService getTimeRangeService() {
         return timeRangeService;
    }

}
