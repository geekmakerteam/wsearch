package org.github.pister.wsearch.core.schedule;

import org.github.pister.wsearch.core.dataprovider.DataProvider;
import org.github.pister.wsearch.core.log.Logger;
import org.github.pister.wsearch.core.log.LoggerFactory;
import org.github.pister.wsearch.core.searcher.SearchEngine;

/**
 * User: longyi
 * Date: 13-10-6
 * Time: 下午10:44
 */
public abstract class AbstractIndexDumpScheduler implements IndexDumpScheduler {

    private Logger log = LoggerFactory.getLogger(getClass());

    private int logPerSize = 1000;

    protected void dump(DataProvider dataProvider, SearchEngine searchEngine) {
        int count = 0;
        try {
            onBeforeDump(dataProvider, searchEngine);

            dataProvider.init();
            onStartingDump(dataProvider, searchEngine);
            while (dataProvider.hasNext()) {
                searchEngine.add(dataProvider.next());
                ++count;
                if (count % logPerSize == 0) {
                    log.warn("dump index count:" + count);
                }
            }
            searchEngine.commitAndOptimize();
            onAfterDump(dataProvider, searchEngine);
        } catch (Throwable t) {
            log.error("dump error", t);
            try {
                onDumpError(dataProvider, searchEngine, t);
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

    @Override
    public void startSchedule(final DataProvider dataProvider, final SearchEngine searchEngine) {
        this.getTrigger().submit(new Runnable() {
            @Override
            public void run() {
                dump(dataProvider, searchEngine);
            }
        });
    }

    protected abstract Trigger getTrigger();


    protected void onBeforeDump(DataProvider dataProvider, SearchEngine searchEngine) {

    }

    protected void onStartingDump(DataProvider dataProvider, SearchEngine searchEngine) {

    }

    protected void onAfterDump(DataProvider dataProvider, SearchEngine searchEngine) {

    }


    protected void onDumpError(DataProvider dataProvider, SearchEngine searchEngine, Throwable t) {

    }


}
