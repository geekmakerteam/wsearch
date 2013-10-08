package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.schedule.handler.ScheduleHandler;
import org.github.pister.wsearch.core.schedule.handler.TimeDelayHandler;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 上午10:09
 */
public class DelayTimeFullScheduler extends FullIndexDumpScheduler {

    private ScheduleHandler scheduleHandler;

    public DelayTimeFullScheduler(int delayTimes) {
        this.scheduleHandler = new TimeDelayHandler(delayTimes);
    }

    @Override
    protected ScheduleHandler getScheduleHandler() {
        return scheduleHandler;
    }
}
