package org.github.pister.wsearch.core.schedule.handler;

import org.github.pister.wsearch.core.schedule.dump.Trigger;
import org.github.pister.wsearch.core.schedule.timerange.TimeRangeService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * User: longyi
 * Date: 13-10-8
 * Time: 上午9:58
 */
public class TimeDelayHandler implements ScheduleHandler {

    private int delayInSeconds;

    private ScheduledFuture<?> scheduledFuture;

    public TimeDelayHandler(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
    }

    @Override
    public void onCancel() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
            scheduledFuture = null;
        }
    }

    @Override
    public Trigger getTrigger() {
        return new Trigger() {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

            @Override
            public void submit(Runnable runnable) {
                scheduledFuture = scheduledExecutorService.scheduleWithFixedDelay(runnable, 0, delayInSeconds, TimeUnit.SECONDS);
            }

        };
    }
}
