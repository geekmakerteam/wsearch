package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.schedule.handler.ScheduleHandler;
import org.github.pister.wsearch.core.schedule.handler.TimeDelayHandler;
import org.github.pister.wsearch.core.schedule.timerange.TimeRangeService;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:40
 */
public class DelayTimeIncrScheduler extends IncrIndexDumpScheduler {

    private ScheduleHandler scheduleHandler;

    public DelayTimeIncrScheduler(int delayInSeconds, TimeRangeService timeRangeService) {
        super(delayInSeconds, timeRangeService);
        this.scheduleHandler = new TimeDelayHandler(delayInSeconds);
    }

    @Override
    protected ScheduleHandler getScheduleHandler() {
        return scheduleHandler;
    }
}
