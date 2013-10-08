package org.github.pister.wsearch.core.schedule.dump;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.log.Logger;
import org.github.pister.wsearch.core.log.LoggerFactory;
import org.github.pister.wsearch.core.schedule.DumpScheduleService;
import org.github.pister.wsearch.core.schedule.SearchEngineSwitchCallback;
import org.github.pister.wsearch.core.schedule.handler.ScheduleHandler;
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
    protected DumpScheduleService dumpScheduleService;
    protected SearchEngineSwitchCallback engineSwitchCallback;
    protected volatile SearchEngine currentSearchEngine;
    protected volatile DataProvider dataProvider;
    private int logPerSize = 1000;
    private AtomicBoolean finish = new AtomicBoolean(false);
    private AtomicBoolean cancel = new AtomicBoolean(false);
    private Lock closeLock = new ReentrantLock();
    private Condition closeCondition = closeLock.newCondition();

    protected void dump() {
        int count = 0;
        DumpContext dumpContext = new DefaultDumpContext();
        dumpContext.setDumpScheduleService(dumpScheduleService);
        SearchEngine searchEngine = null;
        try {
            try {
                finish.set(false);
                onBeforeDump(dumpContext);

                dataProvider.init();
                onStartingDump(dumpContext);

                searchEngine = currentSearchEngine;

                DataProvider dataProvider = this.dataProvider;
                while (!cancel.get() && dataProvider.hasNext()) {
                    searchEngine.add(dataProvider.next());
                    ++count;
                    if (count % logPerSize == 0) {
                        log.warn("dump index count:" + count);
                    }
                }
                dumpContext.setCancel(cancel.get());
                if (cancel.get()) {
                    searchEngine.rollback();
                } else {
                    searchEngine.commitAndOptimize();
                }
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
            if (searchEngine != null) {
                log.warn("rollbacking...");
                searchEngine.rollback();
                log.warn("rollback finish.");
            }
        } finally {
            dataProvider.close();
        }
    }

    protected abstract ScheduleHandler getScheduleHandler();

    public void waitForClose(int waitForSeconds) throws InterruptedException {
        cancel.set(true);
        getScheduleHandler().onCancel();
        try {
            closeLock.lock();
            while (!finish.get()) {
                closeCondition.await(waitForSeconds, TimeUnit.SECONDS);
            }
        } finally {
            closeLock.unlock();
        }
    }

    @Override
    public void startSchedule(DataProvider dataProvider, SearchEngine inputSearchEngine) {
        this.dataProvider = dataProvider;
        this.currentSearchEngine = inputSearchEngine;
        getScheduleHandler().getTrigger().submit(new Runnable() {
            @Override
            public void run() {
                dump();
            }
        });
    }

    @Override
    public void setDumpScheduleService(DumpScheduleService dumpScheduleService) {
        this.dumpScheduleService = dumpScheduleService;
    }

    protected void onBeforeDump(DumpContext dumpContext) {

    }

    protected void onStartingDump(DumpContext dumpContext) {

    }

    protected void onAfterDump(DumpContext dumpContext) {

    }

    protected void onDumpError(DumpContext dumpContext, Throwable t) {

    }

    @Override
    public void setEngineSwitchCallback(SearchEngineSwitchCallback engineSwitchCallback) {
        this.engineSwitchCallback = engineSwitchCallback;
    }
}
