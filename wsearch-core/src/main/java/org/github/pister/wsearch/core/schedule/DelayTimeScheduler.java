package org.github.pister.wsearch.core.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:40
 */
public class DelayTimeScheduler extends AbstractIndexDumpScheduler {

    private int initialDelay;

    private int delay;

    public DelayTimeScheduler(int initialDelay, int delay) {
        this.initialDelay = initialDelay;
        this.delay = delay;
    }

    @Override
    protected Trigger getTrigger() {
        return new Trigger() {

            private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

            @Override
            public void submit(final Runnable runnable) {
                scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.SECONDS);
            }
        };
    }


}
