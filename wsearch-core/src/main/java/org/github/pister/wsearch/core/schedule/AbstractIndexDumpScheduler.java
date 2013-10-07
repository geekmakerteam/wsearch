package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.log.Logger;
import org.github.pister.wsearch.core.log.LoggerFactory;
import org.github.pister.wsearch.core.searcher.SearchEngine;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: longyi
 * Date: 13-10-6
 * Time: 下午10:44
 */
public abstract class AbstractIndexDumpScheduler implements IndexDumpScheduler {

    protected final Logger log = LoggerFactory.getLogger(getClass());
    private int logPerSize = 1000;
    protected DumpScheduleService dumpScheduleService;
    private AtomicBoolean finish = new AtomicBoolean(false);
    private AtomicBoolean cancel = new AtomicBoolean(false);
    private Lock closeLock = new ReentrantLock();
    private Condition closeCondition = closeLock.newCondition();

    protected void dump(DataProvider dataProvider, SearchEngine searchEngine) {
        int count = 0;
        DumpContext dumpContext = new DefaultDumpContext(dataProvider, searchEngine);
        dumpContext.setDumpScheduleService(dumpScheduleService);
        try {
            try {
                finish.set(false);
                onBeforeDump(dumpContext);

                dataProvider.init();
                onStartingDump(dumpContext);
                while (!cancel.get() && dataProvider.hasNext()) {
                    searchEngine.add(dataProvider.next());
                    ++count;
                    if (count % logPerSize == 0) {
                        log.warn("dump index count:" + count);
                    }
                }
                dumpContext.setCancel(cancel.get());
                searchEngine.commitAndOptimize();
                onAfterDump(dumpContext);
            } finally {
                finish.set(true);
            }
            try {
                closeLock.lock();
                closeCondition.signalAll();
            } finally {
                closeLock.unlock();
            }
        } catch (Throwable t) {
            log.error("dump error", t);
            try {
                onDumpError(dumpContext, t);
            } catch (Throwable t2) {
                log.error("on dump error", t2);
            }
            log.warn("rollbacking...");
            searchEngine.rollback();
            log.warn("rollback finish.");
        } finally {
            dataProvider.close();
        }
    }

    public void waitForClose(int waitForSeconds) throws InterruptedException {
        cancel.set(true);
        onCancel();
        try {
            closeLock.lock();
            while (!finish.get()) {
                closeCondition.await(waitForSeconds, TimeUnit.SECONDS);
            }
        } finally {
            closeLock.unlock();
        }
    }

    protected abstract void onCancel();

    @Override
    public void startSchedule(final DataProvider dataProvider, final SearchEngine searchEngine) {
        this.getTrigger().submit(new Runnable() {
            @Override
            public void run() {
                dump(dataProvider, searchEngine);
            }
        });
    }

    @Override
    public void setDumpScheduleService(DumpScheduleService dumpScheduleService) {
        this.dumpScheduleService = dumpScheduleService;
    }

    protected abstract Trigger getTrigger();

    protected void onBeforeDump(DumpContext dumpContext) {

    }

    protected void onStartingDump(DumpContext dumpContext) {

    }

    protected void onAfterDump(DumpContext dumpContext) {

    }

    protected void onDumpError(DumpContext dumpContext, Throwable t) {

    }


}
