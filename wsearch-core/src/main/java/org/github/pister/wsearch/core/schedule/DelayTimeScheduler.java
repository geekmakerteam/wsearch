package org.github.pister.wsearch.core.schedule;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * User: longyi
 * Date: 13-10-7
 * Time: 上午10:40
 */
public class DelayTimeScheduler extends IncrIndexDumpScheduler {

    private int delayInSeconds;

    private TimeRangeService timeRangeService;

    public DelayTimeScheduler(int delayInSeconds) {
        this.delayInSeconds = delayInSeconds;
        this.timeRangeService = new RamTimeRangeService(delayInSeconds);
    }

    @Override
    protected TimeRangeService getTimeRangeService() {
        return timeRangeService;
    }

    @Override
    protected Trigger getTrigger() {
        return new Trigger() {
            ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

            @Override
            public void submit(Runnable runnable) {
                scheduledExecutorService.scheduleWithFixedDelay(runnable, 0, delayInSeconds, TimeUnit.SECONDS);
            }


/*
            @Override
            public void submit(final Runnable runnable) {
                new Thread() {
                    public void run() {
                        try {
                            Thread.sleep(initialDelay * 1000);
                            while (true) {
                               runnable.run();
                               Thread.sleep(delay * 1000);
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }.start();
            }

            */
        };
    }


}
